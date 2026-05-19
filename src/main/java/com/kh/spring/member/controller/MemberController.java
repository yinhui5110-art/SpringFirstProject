package com.kh.spring.member.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.kh.spring.member.model.dto.MemberDto;
import com.kh.spring.member.model.service.MemberServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor // 생성자 주입용 매개변수 생성자를 생성해주는 Lombok 애노테이션
public class MemberController {
	
	//public MemberController() {
		//System.out.println("하이~ 아임 빈"); //서버를 동작시키면 콘솔창에 입력한 내용이 뜬다
										// 톰캣이 web.xml파일을 읽어온다 그다음 root-context.xml에가서 class를 등록하라고 명령한다
	//}
	
	// 1. 값뽑기
	// 2. 가공
	/*
	@RequestMapping("login")
	public void login() {
		System.out.println("로그인 인가?");
	}
	*/
	/*
	@RequestMapping("login")
	public String login(HttpServletRequest request) {
		String userId = request.getParameter("id");
		String userPwd = request.getParameter("pwd");
		
		System.out.printf("id : %s, pwd : %s", userId,userPwd);
		
		return "main";
	}
	
	*/
	/*
	@RequestMapping("login")
	public String login(@RequestParam(value="id")String userId, 
						@RequestParam(value="pwd")String userPwd) {
		System.out.printf("id :%s, pwd : %s", userId,userPwd);
		
		return "main";
	}
	*/
	/*
	@RequestMapping("login")
	public String login(String id, String pwd) {
		System.out.printf("id : %s, pwd : %s", id,pwd);
		return "main";
	}
	*/
	/*
	 * HandlerAdapter의 판단 : 
	 * 1. 매개변수 자리에 기본타입(int, boolean, String, Date..)이 있거나
	 * 		@RequestParam애노테이션이 적혀있는 경우 == @RequestParam으로 인식한다
	 * 
	 * 2. 매개변수 자리에 사용자 정의 클래스(MemberDto, Board, Reply...)이 있거나
	 * 		@ModelAttribute애노테이션이 존재하는 경우 == 커맨드객체 방식으로 인식
	 * 
	 * 커맨드 객체 방식
	 * 
	 * 스프링에서 해당 객체를 기본생성자를 이용해서 생성한 후에 내부적으로 setter매소드를 찾아서
	 * 요청 시 전달값을 해당 필드에 대입해준다
	 * 
	 * 1. 매개변수 자료형에 반드시 기본생성자가 존재 해야한다
	 * 2. setter매서드가 반드시 존재해야 한다
	 * 3. 전달되는 키값과 객체의 필드명이 동일해야 한다
	 */
	//@Autowired
	private final MemberServiceImpl memberService; //권장하지 않는 방법이다
	
	//@Autowired
	//public void setMemberService(MemberService memberService) {
	//	this.memberService = memberService;
	//}
	/*
	@Autowired
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}
	*/
	/*
	@RequestMapping("login")
	public String login(MemberDto member, HttpSession session,
										  Model model) {
		
		// System.out.println(member);
		// new MemberServiceImpl().login(member);
		
		MemberDto userInfo = memberService.login(member);
		//log.info("조회된 사용자의 정보 : {}", userInfo);
		
		// 이전에는 /WEB-INF/views/main.jsp 
		
		// 로그인에 성공 / 실패
		// 응답화면 지정
		
		if(userInfo != null) {
			//sessionScope에 로그인된 사용자의 정보를 set해준다
			// 포워딩 보다는  => sendRedirect => lacalhost:8088/kh/
			session.setAttribute("userInfo", userInfo);
			return "redirect:/"; //전달값이다
			
		}else {
			// 실패했을때 requestScope에 실패메시지를 set
			// 그다음에 /WEB-INF/views/common/error-page.jsp => 포워딩
			model.addAttribute("message", "로그인 실패");
			return "include/error_page";
		}
		
		//return "main";
	}
	*/
	
	// 두번째 방법: 반환타입을 ModelAndView타입으로 반환
	@PostMapping("/login")
	// login은 select가 아니라 post로 해서 예외이다
	public ModelAndView login(MemberDto member, HttpSession session, ModelAndView mv) {
	
		MemberDto userInfo = memberService.login(member);
		
		if(userInfo != null) {
			session.setAttribute("userInfo", userInfo);
			mv.setViewName("redirect:/");
		}else {
			mv.addObject("message", "로그인실패").setViewName("include/error_page");
		}
		return mv;
	}
	
	
	// CRUD 작업
	// 행위 -> .do 자원을 요청보낼것인데 url에 포함시키지 않기로해서
	
	// INSERT => POST
	// SELECT => GET
	// UPDATE => PUT, PATCH
	// DELETE => DELETE
	
	// 내가 지금 요청하는 자원을 적는다
	// localhost:8088/spring/members(기본적으로 복수형을 권장한다)
	
	// POST
	// /members => MEMBER테이블에 한 행 INSERT
	// GET
	// /members => MEMBER테이블에서 여러 행 조회
	// PUT, PATCH
	// /members => MEMBER테이블에서 한 행 UPDATE
	// DELETE
	// /members => MEMBER테이블에서 한 행 삭제
	
	// 한명만 조회 할 때
	// GET
	// /members/1 => MEMBER테이블에서 회원번호가 1번인 회원 조회
	
	
	@GetMapping("logout") // <= 주소 요청 받기이다. 즉 브라우저에서 주소 요청이 들어오면 실행된다
	public String logout(HttpSession session) {
		session.removeAttribute("userInfo");
		return"redirect:/";
	}
	
	@GetMapping("signup")
	public String signupForm() {
		// 포워딩할 JSp파일의 논리적인 경로
		// /WEB-INF/views/       member/signup      .jsp
		return "member/signup";
	}
	
	@PostMapping("members")
	public String signup(MemberDto member //,HttpServletRequest request
			) {
		/*
		try {
			request.setCharacterEncoding("UTF-8");
		}catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		*/
		
		// 아이디, 이름, 비밀번호, 이메일
		log.info("회원가입 정보 :{}", member);
		memberService.signup(member);
		
		return "main";
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
