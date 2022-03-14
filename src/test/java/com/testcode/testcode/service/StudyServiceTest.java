package com.testcode.testcode.service;

import com.testcode.testcode.domain.Member;
import com.testcode.testcode.domain.Study;
import com.testcode.testcode.domain.StudyStatus;
import com.testcode.testcode.repository.StudyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Mock MemberService memberService;
    @Mock StudyRepository studyRepository;

    @Test
    void createStudyService() {
        StudyService studyService = new StudyService(memberService, studyRepository);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("minwoo@email.com");

        // Mock Stubbing
        when(memberService.findById(any())).thenReturn(Optional.of(member)).thenThrow(new IllegalArgumentException()).thenReturn(Optional.empty());

        assertEquals("minwoo@email.com", memberService.findById(1L).get().getEmail());
        assertEquals("minwoo@email.com", memberService.findById(2L).get().getEmail());

        //when(memberService.findById(3L)).thenThrow(new RuntimeException());
        doThrow(new IllegalArgumentException()).when(memberService).validation(1L);

        assertThrows(IllegalArgumentException.class, () -> {
            memberService.validation(1L);
        });

        memberService.validation(2L);
    }

    @Test
    void mockStubbingTest() {
        // Given
        StudyService studyService = new StudyService(memberService,studyRepository);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("minwoo@dev.com");

        Study study = new Study(10,"test");

        // memberService와 studyRepository가 인터페이스로 구현체가 없으므로 동작을 미리 stubbing
        when(memberService.findById(1L)).thenReturn(Optional.of(member));
        given(memberService.findById(1L)).willReturn(Optional.of(member));
        when(studyRepository.save(study)).thenReturn(study);

        // When
        studyService.createNewStudy(1L, study);

        // Then
        assertEquals(member, study.getOwner());

        // memberService의 notify()가 study를 인자로 1번은 동작해야 함
        verify(memberService, times(1)).notify(study);
        then(memberService).should(times(1)).notify(study);
        verify(memberService, never()).validation(any());

        InOrder inOrder = inOrder(memberService);
        inOrder.verify(memberService).notify(study);
        inOrder.verify(memberService).notify(member);

        verifyNoMoreInteractions(memberService);
        then(memberService).shouldHaveNoMoreInteractions();
    }

    @Test
    void openStudy() {

        // Given
        StudyService studyService = new StudyService(memberService, studyRepository);
        Study study = new Study(10, "OPEN STUDY");
        assertNull(study.getOpenDate());
        given(studyRepository.save(study)).willReturn(study);

        // When
        studyService.openStudy(study);

        // Then
        assertEquals(StudyStatus.STARTED, study.getStatus());
        assertNotNull(study.getOpenDate());
        then(memberService).should().notify(study);

    }

}