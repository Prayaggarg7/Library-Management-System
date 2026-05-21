package com.library.controller;

import com.library.dto.MemberDTO;
import com.library.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member Management", description = "Endpoints for managing library members")
@CrossOrigin(origins = "http://localhost:4200")
public class MemberController {
    
    private final MemberService memberService;
    
    @PostMapping
    @Operation(summary = "Create a new member")
    public ResponseEntity<MemberDTO> createMember(@Valid @RequestBody MemberDTO memberDTO) {
        return new ResponseEntity<>(memberService.createMember(memberDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing member")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable Long id, @Valid @RequestBody MemberDTO memberDTO) {
        return ResponseEntity.ok(memberService.updateMember(id, memberDTO));
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update member status (ACTIVE/INACTIVE/SUSPENDED)")
    public ResponseEntity<Void> updateMemberStatus(@PathVariable Long id, @RequestParam String status) {
        memberService.updateMemberStatus(id, status);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/role")
    @Operation(summary = "Update member role (ADMIN/LIBRARIAN/MEMBER)")
    public ResponseEntity<Void> updateMemberRole(@PathVariable Long id, @RequestParam String role) {
        memberService.updateMemberRole(id, role);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a member")
    public ResponseEntity<Void> deactivateMember(@PathVariable Long id) {
        memberService.updateMemberStatus(id, "INACTIVE");
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }
    
    @GetMapping
    @Operation(summary = "Get all members with pagination and sorting")
    public ResponseEntity<Page<MemberDTO>> getAllMembers(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(memberService.getAllMembers(pageable));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search members by name or email")
    public ResponseEntity<Page<MemberDTO>> searchMembers(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(memberService.searchMembers(q, pageable));
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get members by status")
    public ResponseEntity<Page<MemberDTO>> getMembersByStatus(
            @PathVariable String status,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(memberService.getMembersByStatus(status, pageable));
    }
}