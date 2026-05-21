package com.library.mapper;

import com.library.dto.MemberDTO;
import com.library.entity.Member;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MemberMapper {
    
    public MemberDTO toDto(Member member) {
        if (member == null) {
            return null;
        }
        
        MemberDTO dto = new MemberDTO();
        dto.setId(member.getId());
        dto.setName(member.getName());
        dto.setEmail(member.getEmail());
        dto.setStatus(member.getStatus() != null ? member.getStatus().toString() : null);
        dto.setRole(member.getRole() != null ? member.getRole().toString() : null);
        dto.setCreatedAt(member.getCreatedAt());
        
        return dto;
    }
    
    public Member toEntity(MemberDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Member member = new Member();
        member.setId(dto.getId());
        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        if (dto.getStatus() != null) {
            member.setStatus(Member.MemberStatus.valueOf(dto.getStatus()));
        }
        if (dto.getRole() != null) {
            member.setRole(Member.Role.valueOf(dto.getRole()));
        }
        
        return member;
    }
    
    public void updateEntity(MemberDTO dto, Member member) {
        if (dto == null || member == null) {
            return;
        }
        
        if (dto.getName() != null) {
            member.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            member.setEmail(dto.getEmail());
        }
        if (dto.getStatus() != null) {
            member.setStatus(Member.MemberStatus.valueOf(dto.getStatus()));
        }
        if (dto.getRole() != null) {
            member.setRole(Member.Role.valueOf(dto.getRole()));
        }
    }
    
    public List<MemberDTO> toDtoList(List<Member> members) {
        if (members == null) {
            return null;
        }
        return members.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}