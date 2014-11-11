package task_logger

import spock.lang.Specification

class EndToEndTest extends Specification {

    private class ClassUnderTest {
        @TaskLogger
        void task1(){}

        @TaskLogger
        void task2(){
            println "|Println from task2|"
        }
    }

    ClassUnderTest classUnderTest = new ClassUnderTest()

    PrintStream orig
    ByteArrayOutputStream out

    void setup() {
        orig = System.out
        out = new ByteArrayOutputStream()
        System.setOut(new PrintStream(out))
    }

    void cleanup() {
        System.setOut(orig)
    }

    void "an empty method annotated with @TaskLogger will have two println() calls added to it"(){
        when:
            classUnderTest.task1()

        then:
            out.toString() == 'Starting task1\r\nEnding task1\r\n'
    }

    void "println() from a method annotated with @TaskLogger shows up in between the println() calls @TaskLogger adds"(){
        when:
            classUnderTest.task2()

        then:
            out.toString() == 'Starting task2\r\n|Println from task2|\r\nEnding task2\r\n'
    }
}
