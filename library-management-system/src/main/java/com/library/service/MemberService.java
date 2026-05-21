package com.library.service;

import com.library.dto.MemberDTO;
import com.library.entity.Member;
import com.library.exception.ResourceAlreadyExistsException;
import com.library.exception.ResourceNotFoundException;
import com.library.mapper.MemberMapper;
import com.library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberService {
    
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @CacheEvict(value = "members", allEntries = true)
    public MemberDTO createMember(MemberDTO dto) {
        log.info("Creating new member with email: {}", dto.getEmail());
        
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException("Member with email " + dto.getEmail() + " already exists");
        }
        
        Member member = memberMapper.toEntity(dto);
        member.setStatus(Member.MemberStatus.ACTIVE);
        member.setRole(Member.Role.MEMBER);
        member.setPassword(passwordEncoder.encode("default123"));
        
        member = memberRepository.save(member);
        return memberMapper.toDto(member);
    }
    
    @CacheEvict(value = "members", key = "#id")
    public MemberDTO updateMember(Long id, MemberDTO dto) {
        log.info("Updating member with ID: {}", id);
        
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));
        
        if (memberRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new ResourceAlreadyExistsException("Member with email " + dto.getEmail() + " already exists");
        }
        
        // Use the updateEntity method
        memberMapper.updateEntity(dto, member);
        member = memberRepository.save(member);
        return memberMapper.toDto(member);
    }
    
    @CacheEvict(value = "members", key = "#id")
    public void updateMemberStatus(Long id, String status) {
        log.info("Updating member status - ID: {}, Status: {}", id, status);
        
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));
        
        member.setStatus(Member.MemberStatus.valueOf(status));
        memberRepository.save(member);
    }
    
    @CacheEvict(value = "members", key = "#id")
    public void updateMemberRole(Long id, String role) {
        log.info("Updating member role - ID: {}, Role: {}", id, role);
        
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));
        
        member.setRole(Member.Role.valueOf(role));
        memberRepository.save(member);
    }
    
    @Cacheable(value = "members", key = "#id")
    public MemberDTO getMemberById(Long id) {
        log.debug("Fetching member with ID: {}", id);
        
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));
        return memberMapper.toDto(member);
    }
    
    public Page<MemberDTO> getAllMembers(Pageable pageable) {
        log.debug("Fetching all members with pagination");
        return memberRepository.findAll(pageable).map(memberMapper::toDto);
    }
    
    public Page<MemberDTO> searchMembers(String searchTerm, Pageable pageable) {
        log.debug("Searching members with term: {}", searchTerm);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllMembers(pageable);
        }
        return memberRepository.searchMembers(searchTerm.trim(), pageable).map(memberMapper::toDto);
    }
    
    public Page<MemberDTO> getMembersByStatus(String status, Pageable pageable) {
        return memberRepository.findByStatus(Member.MemberStatus.valueOf(status), pageable)
                .map(memberMapper::toDto);
    }
}