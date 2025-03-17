package com.gn.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PageDto {
	
	private int numPerPage = 5;		// 한 페이지에 보이는 데이터 개수
	private int nowPage;			// 현재 페이지
	
	private int pageBarSize = 5;	// 한 페이징바의 페이지 개수
	private int pageBarStart;		// 어디서부터
	private int pageBarEnd;			// 어디까지
	
	private int totalPage;			// 전체 페이지

	private boolean prev = true;	// 이전 버튼 여부
	private boolean next = true;	// 다음 버튼 여부
	
	public void setNowPage(int nowPage) {
		this.nowPage = nowPage;
	}
	
	public void setTotalpage(int totalPage) {
		this.totalPage = totalPage;
		calcPaging();
	}
	
	private void calcPaging() {
		pageBarStart = ((nowPage - 1) / pageBarSize) * pageBarSize + 1; 	// 페이징바 시작 번호 계산
		
		pageBarEnd = pageBarStart + pageBarSize -1; 						// 페이징바 끝 번호 계산
		if(pageBarEnd > totalPage) pageBarEnd = totalPage;
		
		if(pageBarStart == 1) prev = false;									// 이전, 이후 버튼이 보이는지 여부 계산
		if(pageBarEnd >= totalPage) next = false;
	}
}
