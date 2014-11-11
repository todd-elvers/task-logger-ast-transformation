package task_logger

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static task_logger.ASTUtils.*

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class TaskLoggerASTTransformation implements ASTTransformation{

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        if (invalidParameters(nodes)) return

        AnnotationNode annotationInformation = nodes[0] as AnnotationNode
        AnnotatedNode annotatedNode = nodes[1] as AnnotatedNode

        if (annotatedNode instanceof MethodNode) {
            MethodNode methodNode = annotatedNode as MethodNode
            if (methodNode.code instanceof BlockStatement) {
                Statement startPrintlnMessage = createPrintlnStatement("Starting $methodNode.name")
                Statement endPrintlnMessage = createPrintlnStatement("Ending $methodNode.name")

                BlockStatement blockStatement = methodNode.code as BlockStatement
                blockStatement.statements.add(0, startPrintlnMessage)
                blockStatement.statements.add(endPrintlnMessage)
            }
        } else {
            error(source, annotatedNode, "Found @${TaskLogger.simpleName} in an unsupported location. This annotation only works on methods.")
        }
    }

    private static boolean invalidParameters(ASTNode[] nodes) {
        !nodes || !nodes[0] || !nodes[1] || !(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)
    }


}
