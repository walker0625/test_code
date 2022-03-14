package com.testcode.testcode.service;

import com.testcode.testcode.domain.Member;
import com.testcode.testcode.domain.Study;

import java.util.Optional;

public interface MemberService {

    Optional<Member> findById(Long memberId);

    void validation(Long memberId);

    void notify(Study study);

    void notify(Member member);
}
