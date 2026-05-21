package com.library.mapper;

import com.library.dto.TransactionDTO;
import com.library.entity.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionMapper {
    
    public TransactionDTO toDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        
        if (transaction.getBook() != null) {
            dto.setBookId(transaction.getBook().getId());
            dto.setBookTitle(transaction.getBook().getTitle());
        }
        
        if (transaction.getMember() != null) {
            dto.setMemberId(transaction.getMember().getId());
            dto.setMemberName(transaction.getMember().getName());
        }
        
        dto.setIssuedAt(transaction.getIssuedAt());
        dto.setDueDate(transaction.getDueDate());
        dto.setReturnedAt(transaction.getReturnedAt());
        dto.setStatus(transaction.getStatus() != null ? transaction.getStatus().toString() : null);
        dto.setFineAmount(transaction.getFineAmount());
        
        return dto;
    }
    
    public List<TransactionDTO> toDtoList(List<Transaction> transactions) {
        if (transactions == null) {
            return null;
        }
        return transactions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}