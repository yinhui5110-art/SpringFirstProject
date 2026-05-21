package com.kh.spring.member.model.service;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.kh.spring.exception.AuthorizationException;
import com.kh.spring.exception.InvalidParameterException;
import com.kh.spring.exception.TooLargeValueException;
import com.kh.spring.member.model.dao.MemberMapper;
import com.kh.spring.member.model.dto.MemberDto;
import com.kh.spring.member.model.vo.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl{
	
	//private static final Logger log = LoggerFacotry.getLogger(MemberServiceImpl.class);
	//private final MemberDao memberDao;
	//private final SqlSessionTemplate sqlSession;
	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;
	
	/*
	@Autowired
	public MemberServiceImpl(MemberDao memberDao, SqlSessionTemplate sqlSession) {
		this.memberDao = memberDao;
		this.sqlSession = sqlSession;
	}
	스프링의 모든 기능은
	개발자가 직접하던 귀찮은 일을 컨테이너가 대신 해준다
	
	
	
	*/
	
	
	public MemberDto login(MemberDto member) {
		//System.out.println("나는 MemberServiceImpl" + member);//println 사용을 지향한다
		//log.info("인포 메소드로 출력 {},{}", member, member);
		//Login -> ver_1
		/*
		 * SqlSession session = Template.getSqlSession():/Bean으로 등록되어있다
		 * MemberDto loginMember = new MemberDap().login(session, member);/Bean으로 등록되어있다
		 * session.close(); /Bean으로 등록되어있다
		 * return loginMember;
		 * 
		 */
		//spring이 관리할 수 있도록 Bean으로 되어있는 상태이다 그러기 때문에 Bean으로 등록되어있다
		//return memberDao.login(sqlSession, member);
		MemberDto userInfo = memberMapper.login(member);
		// userInfo가 null이라는 것을 로그인에 실패 했다, 로그인 정보가 없다는 의미이다
		/*
		if(userInfo == null) {
			throw new InvalidParameterException("아이디 또는 비밀번호가 틀림");
		}
		
		// 1절
		//log.info("사용자가 입력한 비밀번호 평문 : {}", member.getUserPwd());
		//log.info("DB에 저장된 암호화된 암호문 : {}", userInfo.getUserPwd());
		
		if(passwordEncoder.matches(member.getUserPwd(), userInfo.getUserPwd())){
			return userInfo;
		}
		
		return null;
		*/
		return validateLoginMember(userInfo, member.getUserPwd());
	}
	private MemberDto validateLoginMember(MemberDto userInfo, String userPwd) {
		if(userInfo == null) {
			throw new InvalidParameterException("아이디 또는 비밀번호가 틀림");
		}
		if(passwordEncoder.matches(userPwd, userInfo.getUserPwd())) {
			return userInfo;
		}
		return null;
	}
	
	public void signup(MemberDto member) {
		//memberDao.signup(sqlSession, member);
		/*
		if(member.getUserId().length() > 20) {
			throw new TooLargeValueException("아이디 값이 너무 깁니다.");
		}
		
		if(member.getUserId()== null || 
		   member.getUserId().trim().isEmpty() ||
		   member.getUserPwd() == null || 
		   member.getUserPwd().trim().isEmpty() ||
		   member.getUserName() == null || 
		   member.getUserName().trim().isEmpty()) {
			throw new InvalidParameterException("유효하지 않은 값입니다.");
			
		}
		*/
		
		// 정보에 따른 암호화 분류체계
		// 
		// 비밀번호 				  => 반드시 암호화 해야한다  => 단방향 암호화 (되돌리수 없는)
		// hash 알고리즘 특징 : 데이터를 일정한 길이의 값으로 변환하는 알고리즘입니다.
		// 주민등록번호, 계좌번호, 카드번호  => 반드시 암호화  		 => 양방향 암호화 (되돌릴수 있는)
		// 이름, 이메일, 전화번호		  => 그때 그때 다르다		 => 보통 암호화 하지 않는다 (암호화 하면 검색하지 못하기 때문애)
														// 출력할 때 마스킹
		
		String plainPwd =  member.getUserPwd();
		String encPwd = passwordEncoder.encode(plainPwd);
		
		//Member encMember = new Member(member.getUserId(), encPwd, member.getUserName(),
		//								member.getEmail(), null,null,null);
		Member encMember = Member.builder()
								 .userId(member.getUserId())
								 .userPwd(encPwd)
								 .userName(member.getUserName())
								 .email(member.getEmail())
								 .build();
		//log.info("{}의 암호문 : {}", plainPwd, encPwd);
		//memberMapper.signup(member);
		memberMapper.signup(encMember);
	}
	/*
	 * SRP(Single Responsibility Principle)
	 * 단 일 책 임 원 칙
	 * 하나의 클래스(메소드)는 하나의 책임만을 가져야한다. ==  수정되는 이유는 오로지 딱 한가지여야한다
	 * 
	 */
	private void validateMember(MemberDto member) {
		checkLength(member);
		checkBlank(member);
	}
	
	private void checkLength(MemberDto member) {
		if(member.getUserId().length() > 20) {
			throw new TooLargeValueException("아이디 값이 너무 깁니다.");
		}
		
	}
	/*
	private void checkBlank(MemberDto member) {
		if(member.getUserId()== null || 
		   member.getUserId().trim().isEmpty() ||
		   member.getUserPwd() == null || 
		   member.getUserPwd().trim().isEmpty() ||
		   member.getUserName() == null || 
		   member.getUserName().trim().isEmpty()) {
			throw new InvalidParameterException("유효하지 않은 값입니다.");
		}
	}
	*/
	 //회원정보 수정
	private void checkBlank(MemberDto member) {
		checkUserId(member);
		checkUserPwd(member);
		checkUserName(member);
	}
	
	private void checkUserId(MemberDto member) {
		if(member.getUserId()== null || 
	       member.getUserId().trim().isEmpty()){
			 throw new InvalidParameterException("유효하지 않은 값입니다.");
		}
		
	}
	private void checkUserPwd(MemberDto member) {
		if(member.getUserPwd()== null || 
	       member.getUserPwd().trim().isEmpty()) {
			 throw new InvalidParameterException("유효하지 않은 값입니다.");
				}
	}
	private void checkUserName(MemberDto member) {
		if(member.getUserName()== null || 
	       member.getUserName().trim().isEmpty()) {
			 throw new InvalidParameterException("유효하지 않은 값입니다.");
				}
	}
	
	private void checkNull(MemberDto member) {
		if(member == null) {
			throw new NullPointerException("잘못된 접근입니다");
		}
	}
	
	private void validateUpdateMember(MemberDto member, MemberDto sessionMember) {
		
		checkNull(member);
		checkNull(sessionMember);
		checkUserName(member);
		checkUserId(member);
		
		if(!member.getUserId().equals(sessionMember.getUserId())) {
			throw new AuthorizationException("권한없는 접근입니다");
		}
		checkNull(memberMapper.login(member));
	}
	
	
	
	public void update(MemberDto member, HttpSession session) {
		
		MemberDto sessionMember = ((MemberDto)session.getAttribute("userInfo"));
		
		validateUpdateMember(member, sessionMember);
		
		// memberMapper.update(member);
		// 회원정보 수정을 하기 위한 작업이 필요한다
		
		/* 앞단에서 넘어온 ID값과 현재 로그인된 사용자의 ID값이 일치하는지?
		 * 실제 DB에 ID값이 존재하는 회원인지?
		 * USERNAME컬럼에 넣을 값이 USERNAME컬럼크기보다 크지 않은지?
		 * EMAIL컬럼에 넣을 값이 EMAIL컬럼 크기보다 크지않는지?
		 * USERNAME컬럼에 전달된 값이 빈문자열이 아닌지?
		 */
		
		// DB가서 UPDATE
		int result = memberMapper.update(member);
		
		// 업데이트가 성공적으로 수행되었는지?
		if(result != 1) {
			throw new AuthorizationException("문제가 발생했습니다. 관리자에게 문의하세요.");
		}
		
		// 수정된 정보를 DB에서 SELECT => sessionScope에 존재하는 userInfo키값의 MemberDTO객체 필드값을 갱신해주기
		sessionMember.setUserName(member.getUserName());
		sessionMember.setEmail(member.getEmail());
		
	}
	
	public void delete(String userId, String userpwd, HttpSession session) {
		
		MemberDto sessionMember = ((MemberDto)session.getAttribute("userInfo"));
		
		checkNull(sessionMember);
			
		if(!userId.equals(sessionMember.getUserId())) {
			throw new AuthorizationException("잘못된 요청입니다~");
		}
		
		String encPassword = memberMapper.login(sessionMember).getUserPwd();
		
		if(!passwordEncoder.matches(userpwd, encPassword)){
			throw new AuthorizationException("비밀번호가 일치하지 않습니다.");
			
		}
		int result = memberMapper.delete(userId);
		
		if(result != 1) {
			throw new AuthorizationException("관리자에게 문의하세요.");
		}
		
		session.removeAttribute("userInfo");
	}
	
	public String checkId(String id) {
		return memberMapper.checkId(id);
	}
	
	
	
	
	
	

}
