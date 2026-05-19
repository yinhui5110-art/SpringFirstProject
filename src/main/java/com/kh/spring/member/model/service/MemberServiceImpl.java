package com.kh.spring.member.model.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
	
	
	
	
	
	
	
	
	
	
	
	

}
