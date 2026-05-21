package com.library.service;

import com.library.dto.BookDTO;
import com.library.entity.Book;
import com.library.exception.ResourceAlreadyExistsException;
import com.library.exception.ResourceNotFoundException;
import com.library.mapper.BookMapper;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookService {
    
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    
    @CacheEvict(value = "books", allEntries = true)
    public BookDTO createBook(BookDTO dto) {
        log.info("Creating new book with ISBN: {}", dto.getIsbn());
        
        if (bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new ResourceAlreadyExistsException("Book with ISBN " + dto.getIsbn() + " already exists");
        }
        
        validateBookCopies(dto);
        
        Book book = bookMapper.toEntity(dto);
        book = bookRepository.save(book);
        return bookMapper.toDto(book);
    }
    
    @CacheEvict(value = "books", allEntries = true)
    public BookDTO updateBook(Long id, BookDTO dto) {
        log.info("Updating book with ID: {}", id);
        
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
        
        if (bookRepository.existsByIsbnAndIdNot(dto.getIsbn(), id)) {
            throw new ResourceAlreadyExistsException("Book with ISBN " + dto.getIsbn() + " already exists");
        }
        
        validateBookCopies(dto);
        
        // Use the updateEntity method
        bookMapper.updateEntity(dto, book);
        book = bookRepository.save(book);
        return bookMapper.toDto(book);
    }
    
    @CacheEvict(value = "books", allEntries = true)
    public void deleteBook(Long id) {
        log.info("Deleting book with ID: {}", id);
        
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with ID: " + id);
        }
        bookRepository.deleteById(id);
    }
    
    @Cacheable(value = "books", key = "#id")
    public BookDTO getBookById(Long id) {
        log.debug("Fetching book with ID: {}", id);
        
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
        return bookMapper.toDto(book);
    }
    
    public Page<BookDTO> getAllBooks(Pageable pageable) {
        log.debug("Fetching all books with pagination");
        return bookRepository.findAll(pageable).map(bookMapper::toDto);
    }
    
    public Page<BookDTO> searchBooks(String searchTerm, Pageable pageable) {
        log.debug("Searching books with term: {}", searchTerm);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllBooks(pageable);
        }
        return bookRepository.searchBooks(searchTerm.trim(), pageable).map(bookMapper::toDto);
    }
    
    public Page<BookDTO> getBooksByCategory(String category, Pageable pageable) {
        return bookRepository.findByCategory(category, pageable).map(bookMapper::toDto);
    }
    
    public Page<BookDTO> getRecentBooks(Pageable pageable) {
        return bookRepository.findRecentBooks(pageable).map(bookMapper::toDto);
    }
    
    private void validateBookCopies(BookDTO dto) {
        if (dto.getAvailableCopies() > dto.getTotalCopies()) {
            throw new IllegalArgumentException("Available copies cannot exceed total copies");
        }
    }
}