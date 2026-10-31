package ro.frit.library.repository.member;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ro.frit.library.domain.member.Member;

@Repository 
public class InMemoryMemberRepository implements MemberRepository {
    private Map<String, Member> members = new LinkedHashMap<>();
    private Map<String, String> emailsToIds = new HashMap<>();

    @Override 
    public List<Member> findAll() {
        return List.copyOf(members.values());
    }

    @Override 
    public Optional<Member> findById(String id) {
        return Optional.ofNullable(members.get(id));
    }

    @Override 
    public Optional<Member> findByEmail(String email) {
        String id = emailsToIds.get(normalize(email));
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(members.get(id));
    }

    @Override 
    public Member save(Member member) {
        String id = member.getId();
        String emailKey = normalize(member.getEmail());

        String ownerId = emailsToIds.get(emailKey);
        if (ownerId != null && !ownerId.equals(id)) {
            throw new IllegalStateException("Email already in use");
        }

        Member existing = members.get(id);
        if (existing != null) {
            String oldEmailKey = normalize(existing.getEmail());
            if (!oldEmailKey.equals(emailKey)) {
                emailsToIds.remove(oldEmailKey);
            }
        }

        emailsToIds.put(emailKey, id);
        members.put(id, member);
        return member;
    }

    @Override
    public boolean deleteById(String id) {
        Member removed = members.remove(id);
        if (removed == null) {
            return false;
        }
        emailsToIds.remove(normalize(removed.getEmail()));
        return true;
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase();
    }
}
