package com.library.mapper;

import com.library.dto.BookDTO;
import com.library.entity.Book;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookMapper {
    
    public BookDTO toDto(Book book) {
        if (book == null) {
            return null;
        }
        
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setCategory(book.getCategory());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setAvailableCopies(book.getAvailableCopies());
        dto.setShelfLocation(book.getShelfLocation());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());
        
        return dto;
    }
    
    public Book toEntity(BookDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setCategory(dto.getCategory());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getAvailableCopies());
        book.setShelfLocation(dto.getShelfLocation());
        
        return book;
    }
    
    public void updateEntity(BookDTO dto, Book book) {
        if (dto == null || book == null) {
            return;
        }
        
        if (dto.getTitle() != null) {
            book.setTitle(dto.getTitle());
        }
        if (dto.getAuthor() != null) {
            book.setAuthor(dto.getAuthor());
        }
        if (dto.getIsbn() != null) {
            book.setIsbn(dto.getIsbn());
        }
        if (dto.getCategory() != null) {
            book.setCategory(dto.getCategory());
        }
        if (dto.getTotalCopies() != null) {
            book.setTotalCopies(dto.getTotalCopies());
        }
        if (dto.getAvailableCopies() != null) {
            book.setAvailableCopies(dto.getAvailableCopies());
        }
        if (dto.getShelfLocation() != null) {
            book.setShelfLocation(dto.getShelfLocation());
        }
    }
    
    public List<BookDTO> toDtoList(List<Book> books) {
        if (books == null) {
            return null;
        }
        return books.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}