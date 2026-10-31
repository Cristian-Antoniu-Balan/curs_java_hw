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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ro.frit.library.service.member.MemberService;

@Tag(name = "Members", description = "Members catalog")
@RestController 
@RequestMapping("/members")
@RequiredArgsConstructor 
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "List members")
    @GetMapping
    public List<MemberResponse> getAllMembers() {
        return memberService.findAll().stream()
            .map(MemberResponse::from)
            .toList();
    }

    @Operation(summary = "Get member by id")
    @ApiResponse(responseCode = "404", description = "Member not found")
    @GetMapping("/{id}")
    public MemberResponse member(@PathVariable("id") String id) {
        return memberService.findById(id)
            .map(MemberResponse::from)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No member with id: " + id));
    }

    @Operation(summary = "Create member")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse postMember(@Valid @RequestBody MemberRequest memberRequest) {
        return MemberResponse.from(memberService.createMember(memberRequest));
    }

    @Operation(summary = "Replace member")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Replaced"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "404", description = "Member not found"),
        @ApiResponse(responseCode = "409", description = "Email already in use by another member")
    })
    @PutMapping("/{id}")
    public MemberResponse putMember(
        @PathVariable("id") String id,
        @Valid @RequestBody MemberRequest memberRequest) {
            return memberService.replace(id, memberRequest)
                .map(MemberResponse::from)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Member not found for id: " + id));
        }
    
    @Operation(summary = "Delete member")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deleted"),
        @ApiResponse(responseCode = "404", description = "Member not found"),
        @ApiResponse(responseCode = "409", description = "Member has active loans")
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        if (!memberService.delete(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Member not found for id: " + id);
        }
    }
}
