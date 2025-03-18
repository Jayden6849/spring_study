package com.gn.mvc.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gn.mvc.entity.Attach;

public interface AttachRepository extends JpaRepository<Attach, Long> {
	
	// findAll 메소드를 사용할 때 spec이라는 매개변수를 쓸 수 있도록 오버로딩 및 오버라이딩
	List<Attach> findAll(Specification<Attach> spec);
	
}
