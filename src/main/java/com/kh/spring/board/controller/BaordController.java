package com.kh.spring.board.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.kh.spring.board.model.dto.BoardDto;
import com.kh.spring.board.model.service.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("boards")
@RequiredArgsConstructor
public class BaordController {
	
	private final BoardService boardService;
	
	/*
	 * mapping 값으로
	 * 
	 * 전체조회   == boards  			== GET    => 페이징처리
	 * 상세조회   == boards/{baordNo}  == GET
	 * 작성	   == boards            == POST
	 * 
	 * SELECT == GET / INSERT == POST	
	 */
	
	//@GetMapping("boards")
	//@GetMapping("boards/{boardNo}")
	//@GetMapping("boards")

	@GetMapping
	public ModelAndView finAll(ModelAndView mv, 
								@RequestParam (name="page", defaultValue="1")int page) { 
	//boards에서 출력할 내용에 모델에 담아가야 하기 때문에 ModelAndView
		Map<String, Object> map = boardService.findAll(page);
		mv.addObject("map", map).setViewName("board/boards");
		return mv;
	}	
		
	@GetMapping("/form")
	public String toForm() {
		return "board/form";
	}
	
	@PostMapping
	public String save(BoardDto board, MultipartFile upfile, HttpSession session) {
		//log.info("게시글 정보 : {}, 첨부파일 정보 : {}", board, upfile);
		// 첨부파일의 존재 유무
		//MultipartFile객체의 fileName필드값으로 확인해야한다
		/*
		 * 1. 권한있는 요청인가
		 * 2. 파일 존재유무 체크 => 이름 바꾸기 작업 => 파일 업로드
		 * 3. 값의 유효성 검사
		 */
		boardService.save(board, upfile, session);
		return "redirect:boards";
	}
	/*
	@GetMapping("/{id}")
	public ModelAndView toDetail(@PathVariable(value="id") Long boardNo,
									ModelAndView mv) {
		
		boardService.findByBoardNo(boardNo);
		
		mv.setViewName("board/detail");
		return mv;
		
	}
	*/
	
	
	
	
	
	
	
	
	
	
	
	
}
