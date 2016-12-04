package te.ast.task_logger;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@GroovyASTTransformationClass(classes = {TaskLoggerASTTransformation.class})
public @interface TaskLogger {

    /*
     * Add some way to better configure the logging style.  e.g.
     *      - "Starting <method>." & "Ending <method>."
     *      - "Starting <method>...done!"
     */

}
