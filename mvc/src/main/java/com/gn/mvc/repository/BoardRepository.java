package com.gn.mvc.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gn.mvc.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {
	
	// Method Naming 방식
	List<Board> findByBoardTitleContaining(String keyword); // 제목 기준으로 List<Board> 를 조회하는 추상메소드
	List<Board> findByBoardContentContaining(String keyword);
	List<Board> findByBoardTitleContainingOrBoardContentContaining(String titleKeyword, String contentKeyword);
	
	// JPQL 방식
	@Query(value="SELECT b FROM Board b WHERE b.boardTitle LIKE CONCAT('%',:keyword,'%')")
	List<Board> findByTitleLike(@Param("keyword") String keyword);
	
	@Query(value="SELECT b FROM Board b WHERE b.boardContent LIKE CONCAT('%', ?1,'%')")
	List<Board> findByContentLike(String keyword);
	
	@Query(value="SELECT b FROM Board b WHERE b.boardTitle LIKE CONCAT('%', ?1,'%') OR b.boardContent LIKE CONCAT('%', ?2,'%')")
	List<Board> findByTitleOrContentLike(String title, String content);
	
	// Specification 방식 - 오버로딩한 메소드를 오버라이딩
	List<Board> findAll(Specification<Board> spec);
	
}
