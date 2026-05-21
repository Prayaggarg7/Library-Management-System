package com.library.service;

import com.library.dto.IssueRequestDTO;
import com.library.dto.ReturnRequestDTO;
import com.library.dto.TransactionDTO;
import com.library.entity.Book;
import com.library.entity.Member;
import com.library.entity.Transaction;
import com.library.exception.ResourceNotFoundException;
import com.library.exception.ValidationException;
import com.library.mapper.TransactionMapper;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import com.library.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final TransactionMapper transactionMapper;
    
    private static final int DEFAULT_LOAN_DAYS = 14;
    private static final BigDecimal FINE_PER_DAY = BigDecimal.valueOf(0.50);
    private static final BigDecimal MAX_FINE = BigDecimal.valueOf(50.00);
    
    @CacheEvict(value = "transactions", allEntries = true)
    public TransactionDTO issueBook(IssueRequestDTO request) {
        log.info("Issuing book - Book ID: {}, Member ID: {}", request.getBookId(), request.getMemberId());
        
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + request.getBookId()));
        
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + request.getMemberId()));
        
        if (member.getStatus() != Member.MemberStatus.ACTIVE) {
            throw new ValidationException("Member is not active. Cannot issue books.");
        }
        
        if (book.getAvailableCopies() <= 0) {
            throw new ValidationException("Book is not available for issue. No copies left.");
        }
        
        // Check for existing active transaction
        var existingTransaction = transactionRepository.findByBookIdAndMemberIdAndStatus(
                request.getBookId(), request.getMemberId(), Transaction.TransactionStatus.ISSUED);
        if (existingTransaction.isPresent()) {
            throw new ValidationException("Member already has this book issued. Please return it first.");
        }
        
        Transaction transaction = new Transaction();
        transaction.setBook(book);
        transaction.setMember(member);
        transaction.setIssuedAt(LocalDateTime.now());
        transaction.setDueDate(request.getDueDate() != null ? request.getDueDate() : LocalDate.now().plusDays(DEFAULT_LOAN_DAYS));
        transaction.setStatus(Transaction.TransactionStatus.ISSUED);
        transaction.setFineAmount(BigDecimal.ZERO);
        
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        
        transaction = transactionRepository.save(transaction);
        
        log.info("Book issued successfully. Transaction ID: {}", transaction.getId());
        return transactionMapper.toDto(transaction);
    }
    
    @CacheEvict(value = "transactions", allEntries = true)
    public TransactionDTO returnBook(ReturnRequestDTO request) {
        log.info("Returning book - Transaction ID: {}", request.getTransactionId());
        
        Transaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + request.getTransactionId()));
        
        if (transaction.getStatus() == Transaction.TransactionStatus.RETURNED) {
            throw new ValidationException("Book has already been returned.");
        }
        
        // Calculate fine if overdue
        LocalDate returnDate = LocalDate.now();
        BigDecimal fine = calculateFine(transaction.getDueDate(), returnDate);
        
        if (fine.compareTo(BigDecimal.ZERO) > 0) {
            transaction.setFineAmount(fine);
            log.info("Overdue book. Fine calculated: ${}", fine);
        }
        
        transaction.setReturnedAt(LocalDateTime.now());
        transaction.setStatus(Transaction.TransactionStatus.RETURNED);
        
        Book book = transaction.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        
        transaction = transactionRepository.save(transaction);
        
        log.info("Book returned successfully. Transaction ID: {}", transaction.getId());
        return transactionMapper.toDto(transaction);
    }
    
    private BigDecimal calculateFine(LocalDate dueDate, LocalDate returnDate) {
        if (returnDate.isAfter(dueDate)) {
            long daysOverdue = ChronoUnit.DAYS.between(dueDate, returnDate);
            BigDecimal fine = FINE_PER_DAY.multiply(BigDecimal.valueOf(daysOverdue));
            return fine.min(MAX_FINE);
        }
        return BigDecimal.ZERO;
    }
    
    @Scheduled(cron = "0 0 1 * * ?") // Run daily at 1 AM
    @Transactional
    public void processOverdueTransactions() {
        log.info("Processing overdue transactions - Scheduled task");
        
        List<Transaction> overdueTransactions = transactionRepository.findOverdueTransactions();
        int updatedCount = 0;
        
        for (Transaction transaction : overdueTransactions) {
            if (transaction.getStatus() == Transaction.TransactionStatus.ISSUED) {
                transaction.setStatus(Transaction.TransactionStatus.OVERDUE);
                transactionRepository.save(transaction);
                updatedCount++;
                log.debug("Marked transaction {} as overdue", transaction.getId());
            }
        }
        
        log.info("Processed {} overdue transactions", updatedCount);
    }
    
    @Cacheable(value = "transactions", key = "#transactionId")
    public TransactionDTO getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .map(transactionMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }
    
    public Page<TransactionDTO> getTransactionsByMember(Long memberId, Pageable pageable) {
        return transactionRepository.findByMemberId(memberId, pageable)
                .map(transactionMapper::toDto);
    }
    
    public Page<TransactionDTO> getTransactionsByBook(Long bookId, Pageable pageable) {
        return transactionRepository.findByBookId(bookId, pageable)
                .map(transactionMapper::toDto);
    }
    
    public Page<TransactionDTO> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable).map(transactionMapper::toDto);
    }
    
    public Page<TransactionDTO> filterTransactions(String status, Long memberId, Long bookId, Pageable pageable) {
        Transaction.TransactionStatus transactionStatus = null;
        if (status != null && !status.isEmpty()) {
            transactionStatus = Transaction.TransactionStatus.valueOf(status);
        }
        return transactionRepository.findWithFilters(transactionStatus, memberId, bookId, pageable)
                .map(transactionMapper::toDto);
    }
}