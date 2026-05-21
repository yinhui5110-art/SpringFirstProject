package com.kh.spring.board.model.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.board.model.dao.BoardMapper;
import com.kh.spring.board.model.dto.BoardDto;
import com.kh.spring.exception.AuthorizationException;
import com.kh.spring.exception.InvalidParameterException;
import com.kh.spring.member.model.dto.MemberDto;
import com.kh.spring.util.PageInfo;
import com.kh.spring.util.Pagination;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
	
	private final BoardMapper boardMapper;
	private final Pagination pagenation;
	
	
	/*
	 * 1. @Transactional은 public메서드에서만 동작한다
	 * 
	 * 2. 같은 클래스 안에서 매서드끼리 호출하면 트랜잭션 안걸린다
	 * 
	 * 3. CheckedException은 기본적으로 롤백 안된다
	 * 
	 * 4. 의존성 이슈
	 */
	
	
	
	// 안정장치 없음X => 성능 최적화  + 의도표시
	@Transactional(readOnly = true)// DB작업할 때 이 트랜잭션은 데이터 변경안합니다 => 힌트
	public Map findAll(int page) {
		
		// 유효성검증
		
		if(page < 1) {
			throw new InvalidParameterException("잘못된 접근입니다.");
		}
		// 실직적인 비즈니스 로직 =>  페리징 처리를 위한 PageInfo객체 생성 및 페이징 처리후 게시글 조회
		
		int totalCount = boardMapper.selectTotalCount();
		
		log.info("총 게시글 개수 : {}", totalCount);
		PageInfo pi = pagenation.getPageInfo(totalCount, page, 5, 3);
		
		RowBounds rb = new RowBounds((page - 1) * 5, 5);
		List<BoardDto> boards = boardMapper.findAll(rb);
		log.info("게시글 목록 : {}", boards);
		
		return Map.of("boards", boards,"pi", pi);
	}
	
	
	// <tx:annotation-driven이 켜지면 스프링이 @Transactional이 붙은 메서드를 발견해서 프록시로 감싼다
	// 프록시 객체 내부에서 save()메소드를 호출할 때 connection.setAutoCommit(false)로 돌리고 호출
	// 그 후 메소드 종료시 commit()을 호출 예외 발생시 rollback()
	@Transactional 
	public void save(BoardDto board, MultipartFile upfile, HttpSession session) {
		
		// 1. 권한검증
		validateUser(board, session);
		
		// 2. 유효성검증
		validateContent(board);
		
		// 3. 파일이 있을경우 이름을 바꿔서 서버에 업로드 => 파일의 정보를 board의 필드에 대입
		fileUpload(upfile, board,session);
		
		// 4. 
		int result = boardMapper.save(board);
		
		if(result != 1) {
			throw new RuntimeException("관리자에게 문의하세요.");
		}
	
	}
	private void fileUpload(MultipartFile upfile, BoardDto board, HttpSession session) {
		
		if(!upfile.getOriginalFilename().isEmpty()) {
			// 이름 바꾸기
			StringBuilder sb = new StringBuilder();
			sb.append("KH_");
			sb.append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			sb.append("_");
			sb.append((int)(Math.random()*900)+ 100);
			sb.append(upfile.getOriginalFilename().substring(upfile.getOriginalFilename().lastIndexOf(".")));
			
			// 파일 업로드
			ServletContext application = session.getServletContext();
			String savePath = application.getRealPath("/resources/files/");
			
			try {
				upfile.transferTo(new File(savePath + sb.toString()));
			}catch(IllegalStateException | IOException e) {
				e.printStackTrace();
			}
			
			// board setting
			board.setOriginName(upfile.getOriginalFilename());
			board.setChangeName("/spring/resources/files/" + sb.toString());
		}
		
	}
	
	
	
	
	
	private void validateUser(BoardDto board, HttpSession session) {
		String boardWriter = board.getBoardWriter();
		MemberDto userInfo = (MemberDto)session.getAttribute("userInfo");
		
		if(userInfo == null || !userInfo.getUserId().equals(boardWriter)) {
			throw new AuthorizationException("권한없는 요청입니다.");
		}
	}
	private void validateContent(BoardDto board) {
		if(board.getBoardTitle().trim().isEmpty()|| board.getBoardContent().trim().isEmpty()) {
			throw new InvalidParameterException("유효하지 않은 요청입니다.");
		}
		String boardTitle = board.getBoardTitle().replaceAll("<", "&lt;").replaceAll("\n", "<br>");
		String boardContent = board.getBoardContent().replaceAll("<","&lt;").replaceAll("\n", "<br>");
		if(board.getBoardTitle().contains("ㅎㅎㅎㅎ")) {
			boardTitle = board.getBoardTitle().replaceAll("ㅎㅎㅎㅎ","ㅋㅋㅋㅋ");
		}
		board.setBoardTitle(boardTitle);
		board.setBoardContent(boardContent);
	
	}
	
	//public BoardDto findByBoardNo(Long boardNo) {
		
	//	if(boardNo < 1 || boardNo == null) {
		//	throw new InvalidParameterException("유효하지 않은 요청입니다.");
	//	}
	//	int result = boardMapper.increaseCount(boardNo);
	//	
	//	if(result != 1 ) {
	//		throw new InvalidParameterException("잘못된 요청입니다.");
	//	}
	//	BoardDto board = boardMapper.findByBoardNo(boardNo);
	//}
	
	
	


	public int count() {
		return boardMapper.selectTotalCount();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
