package com.library.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author must not exceed 255 characters")
    private String author;
    
    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^[0-9-]{10,20}$", message = "Invalid ISBN format")
    private String isbn;
    
    private String category;
    
    @NotNull(message = "Total copies is required")
    @Min(value = 0, message = "Total copies must be at least 0")
    private Integer totalCopies;
    
    @NotNull(message = "Available copies is required")
    @Min(value = 0, message = "Available copies must be at least 0")
    private Integer availableCopies;
    
    private String shelfLocation;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}