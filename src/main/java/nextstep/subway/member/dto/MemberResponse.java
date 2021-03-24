package nextstep.subway.member.dto;

import nextstep.subway.auth.domain.UserDetails;
import nextstep.subway.member.domain.LoginMember;
import nextstep.subway.member.domain.Member;

public class MemberResponse {
    private Long id;
    private String email;
    private Integer age;

    private MemberResponse() {
    }

    private MemberResponse(Long id, String email, Integer age) {
        this.id = id;
        this.email = email;
        this.age = age;
    }

    private static MemberResponse of(Long id, String email, Integer age) {
        return new MemberResponse(id, email, age);
    }

    public static MemberResponse of(Member member) {
        return of(member.getId(), member.getEmail(), member.getAge());
    }

    public static MemberResponse of(LoginMember loginMember) {
        return of(loginMember.getId(), loginMember.getEmail(), loginMember.getAge());
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }
}