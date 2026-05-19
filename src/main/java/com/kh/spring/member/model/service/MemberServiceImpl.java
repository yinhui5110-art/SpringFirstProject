package com.kh.spring.member.model.service;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.member.model.dao.MemberDao;
import com.kh.spring.member.model.dto.MemberDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl{
	
	//private static final Logger log = LoggerFacotry.getLogger(MemberServiceImpl.class);
	private final MemberDao memberDao;
	private final SqlSessionTemplate sqlSession;
	
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
		return memberDao.login(sqlSession, member);
	
	}
	public void signup(MemberDto member) {
		memberDao.signup(sqlSession, member);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
