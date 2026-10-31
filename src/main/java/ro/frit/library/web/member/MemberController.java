package ro.frit.library.web.member;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ro.frit.library.service.member.MemberService;

@RestController 
@RequestMapping("/members")
@RequiredArgsConstructor 
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public List<MemberResponse> getAllMembers() {
        return memberService.findAll().stream()
            .map(MemberResponse::from)
            .toList();
    }

    @GetMapping("/{id}")
    public MemberResponse member(@PathVariable("id") String id) {
        return memberService.findById(id)
            .map(MemberResponse::from)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No member with id: " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse postMember(@Valid @RequestBody MemberRequest memberRequest) {
        return MemberResponse.from(memberService.createMember(memberRequest));
    }

    @PutMapping("/{id}")
    public MemberResponse putMember(
        @PathVariable("id") String id,
        @Valid @RequestBody MemberRequest memberRequest) {
            return memberService.replace(id, memberRequest)
                .map(MemberResponse::from)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Member not found for id: " + id));
        }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        if (!memberService.delete(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Member not found for id: " + id);
        }
    }
}
