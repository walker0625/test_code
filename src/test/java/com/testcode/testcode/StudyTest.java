package com.testcode.testcode;

import com.testcode.testcode.domain.Study;
import com.testcode.testcode.domain.StudyStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 모든 테스트가 하나의 클래스를 공유함(클래스 내 값을 공유)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudyTest {

    @Order(3)
    @Test
    @EnabledOnOs({OS.WINDOWS, OS.MAC})
    @EnabledOnJre(JRE.JAVA_8)
    @DisplayName("maked name")
    @Tag("fast")
    void assertAllTest() {

        Study study = new Study(1);
        String env = "LOCAL";

        assumeTrue("LOCAL".equalsIgnoreCase(env)); //이 조건을 넘어가지않으면 아래 테스트가 실행되지 않음 - System.getenv()

        assumingThat("LOCAL".equalsIgnoreCase(env),
                () -> System.out.println("env Local")
        );

        assertAll (
                () -> assertNotNull(study),
                () -> assertEquals(StudyStatus.STARTED, study.getStatus(), () -> "스터디의 초기" + "상태값은"  + "DRAFT여야 한다."), // 문자열 연산을 실패할 때만 하고 싶을 때는 람다식 표현(성능)
                () -> assertTrue(study.getLimit() > 0, "참가자 수는 0보다 커야함")
        );

    }

    @Order(2)
    @EnabledOnJre({JRE.JAVA_13, JRE.JAVA_18}) // 실행 안됨
    @EnabledIfEnvironmentVariable(named="test_env", matches="local") // test_env의 값이 local일때만 실행됨
    @Test
    @Tag("slow")
    void error_throw_test() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-1));
        String message = exception.getMessage();
        assertEquals("limit는 0보다 커야함", message);
    }

    @Order(1)
    @Test
    void duration_test() {
            // 얼마나 초과 되었는지는 확인안됨(assertTimeout()은 가능 - ThreadLocal(transaction에 독립적)에도 안전)
            assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            new Study(10);
            Thread.sleep(3000);
        });
    }

    @DisplayName("반복 테스트")
    @RepeatedTest(value = 10, name = "{displayName}, {currentRepetition}/{totalRepetitions}")
    void repeatTest(RepetitionInfo repetitionInfo) {
        System.out.println("repeat Test");
        System.out.println("repeatTest : "+ repetitionInfo.getCurrentRepetition());
        System.out.println("repeatTest : "+ repetitionInfo.getTotalRepetitions());
    }

    @DisplayName("파라미터 테스트")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    //@ValueSource(strings = {"날씨", "영하", "10도"})
    @EmptySource
    @NullSource
    @NullAndEmptySource
    void parameterTest(String message) {
        System.out.println(message);
    }

    @DisplayName("컨버터 테스트")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @ValueSource(ints = {10, 20, 40})
    void converterTest(@ConvertWith(StudyConverter.class) Study study) {
        System.out.println(study.getLimit());
    }

    static class StudyConverter extends SimpleArgumentConverter {
        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "Convert Study");
            return new Study(Integer.parseInt(source.toString()));
        }
    }

    static class StudyAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor aa, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new Study(aa.getInteger(0), aa.getString(1));
        }
    }

    @DisplayName("csv 테스트")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, '자바 테스트'","20, '스프링'"})
    void csvTest(@AggregateWith(StudyAggregator.class) Study study) { // Integer limit, String name > ArgumentsAccessor aa > StudyAggregator
        //Study study = new Study(aa.getInteger(0), aa.getString(1));
        System.out.println(study);
    }

    @Test
    @Disabled
    void create3() {
        System.out.println("create3");
    }

    @BeforeAll
    static void beforeAll(){
        System.out.println("before only one");
    }

    @AfterAll
    static void  afterAll() {
        System.out.println("after only one");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("before all each");
    }

    @AfterEach
    void afterEach() {
        System.out.println("after all each");
    }

}