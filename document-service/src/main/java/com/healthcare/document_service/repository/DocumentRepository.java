package com.healthcare.document_service.repository;

import com.healthcare.document_service.entity.Document;
import com.healthcare.document_service.entity.URLDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {
}
