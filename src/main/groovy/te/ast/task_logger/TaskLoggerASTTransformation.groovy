package te.ast.task_logger

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation


@CompileStatic
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class TaskLoggerASTTransformation implements ASTTransformation {

    private static final String UNSUPPORTED_LOCATION = "Found @${TaskLogger.simpleName} in an unsupported location. This annotation only works on methods."

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        if (invalidParameters(nodes)) return

        AnnotationNode annotationInformation = nodes[0] as AnnotationNode
        AnnotatedNode annotatedNode = nodes[1] as AnnotatedNode

        if (annotatedNode instanceof MethodNode) {
            MethodNode methodNode = annotatedNode as MethodNode
            if (methodNode.code instanceof BlockStatement) {
                Statement startPrintlnStatement = createPrintlnStatement("Starting $methodNode.name.")
                Statement endPrintlnStatement = createPrintlnStatement("Ending $methodNode.name.")

                BlockStatement blockStatement = methodNode.code as BlockStatement
                blockStatement.statements.add(0, startPrintlnStatement)
                blockStatement.statements.add(endPrintlnStatement)
            }
        } else {
            error(source, annotatedNode, UNSUPPORTED_LOCATION)
        }
    }

    private static boolean invalidParameters(ASTNode[] nodes) {
        !nodes || !nodes[0] || !nodes[1] || !(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)
    }

    /**
     * @return an expression that represents: <code>this.println(message);</code>
     */
    private static ExpressionStatement createPrintlnStatement(String message) {
        return new ExpressionStatement(
                new MethodCallExpression(
                        new VariableExpression("this"),
                        new ConstantExpression("println"),
                        new ArgumentListExpression(
                                new ConstantExpression(message)
                        )
                )
        )
    }

    /**
     * Generates a fatal compilation error.
     *
     * @param sourceUnit the SourceUnit
     * @param astNode the ASTNode which caused the error
     * @param message The error message
     */
    private static void error(final SourceUnit sourceUnit, final ASTNode astNode, final String message) {
        final SyntaxException syntaxException = new SyntaxException(message, astNode.getLineNumber(), astNode.getColumnNumber())
        final SyntaxErrorMessage syntaxErrorMessage = new SyntaxErrorMessage(syntaxException, sourceUnit)

        sourceUnit.getErrorCollector().addError(syntaxErrorMessage, true)
    }
}
