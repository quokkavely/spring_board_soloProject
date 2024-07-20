package com.springboot.member.cotroller;

import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.springboot.response.MultiResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@Validated
@RequestMapping("/v1/members")
@RestController
public class MemberController {
    private final MemberMapper mapper;
    private final MemberService memberService;
    private final static String MEMBER_DEFAULT_URL="/v1/members";

    public MemberController(MemberMapper mapper, MemberService memberService) {
        this.mapper = mapper;
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity postMember(@Valid @RequestBody MemberDto.Post postDto){
       Member member = memberService.createMember(mapper.memberPostDtoToMember(postDto));
       URI location = UriComponentsBuilder
               .newInstance()
               .path(MEMBER_DEFAULT_URL + "/{memberId}")
               .buildAndExpand(member.getMemberId())
               .toUri();

       return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity patchMember(@PathVariable("memberId") @Positive long memberId,
                                     @Valid @RequestBody MemberDto.Patch patchDto){
        patchDto.setMemberId(memberId);
        Member member = memberService.updateMember(mapper.memberPatchDtoToMember(patchDto));
        return new ResponseEntity<>(mapper.memberToResponseDto(member),HttpStatus.OK);

    }

    @GetMapping("/{memberId}")
    public ResponseEntity getMember(@PathVariable("memberId") @Positive long memberId){
        Member member=memberService.findMember(memberId);
        return new ResponseEntity(mapper.memberToResponseDto(member),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getMembers(@Positive @RequestParam int size,
                                   @Positive @RequestParam int page){

        Page<Member>pageMembers = memberService.findMembers(page-1, size);
        List<Member> members= pageMembers.getContent();
        return new ResponseEntity<>(
                new MultiResponseDto<>(
                        mapper.membersToResponseDto(members),pageMembers),HttpStatus.OK); //PageInfo, pagereponse 구현
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity deleteMember (@RequestParam @Positive long memberId){
        memberService.deleteMember(memberId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
