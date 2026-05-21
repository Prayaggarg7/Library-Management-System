package com.library.repository;

import com.library.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByBookIdAndMemberIdAndStatus(Long bookId, Long memberId, Transaction.TransactionStatus status);
    
    Page<Transaction> findByMemberId(Long memberId, Pageable pageable);
    
    Page<Transaction> findByBookId(Long bookId, Pageable pageable);
    
    Page<Transaction> findByStatus(Transaction.TransactionStatus status, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.status = 'ISSUED' AND t.dueDate < CURRENT_DATE")
    List<Transaction> findOverdueTransactions();
    
    @Query("SELECT t FROM Transaction t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:memberId IS NULL OR t.member.id = :memberId) AND " +
           "(:bookId IS NULL OR t.book.id = :bookId)")
    Page<Transaction> findWithFilters(@Param("status") Transaction.TransactionStatus status,
                                       @Param("memberId") Long memberId,
                                       @Param("bookId") Long bookId,
                                       Pageable pageable);
}