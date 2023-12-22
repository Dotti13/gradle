package com.h0tk3y.kotlin.staticObjectNotation.astToLanguageTree

import com.h0tk3y.kotlin.staticObjectNotation.language.SourceData
import com.h0tk3y.kotlin.staticObjectNotation.language.SourceIdentifier
import org.jetbrains.kotlin.KtNodeTypes.*
import org.jetbrains.kotlin.com.intellij.lang.LighterASTNode
import org.jetbrains.kotlin.com.intellij.lang.impl.PsiBuilderImpl
import org.jetbrains.kotlin.com.intellij.openapi.util.Ref
import org.jetbrains.kotlin.com.intellij.psi.TokenType.ERROR_ELEMENT
import org.jetbrains.kotlin.com.intellij.psi.TokenType.WHITE_SPACE
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.com.intellij.util.diff.FlyweightCapableTreeStructure
import org.jetbrains.kotlin.diagnostics.isExpression
import org.jetbrains.kotlin.lexer.KtTokens.*

typealias LightTree = FlyweightCapableTreeStructure<LighterASTNode>

fun FlyweightCapableTreeStructure<LighterASTNode>.sourceData(
    sourceIdentifier: SourceIdentifier,
    sourceCode: String,
    sourceOffset: Int
) =
    LightTreeSourceData(
        sourceIdentifier,
        sourceCode,
        sourceOffset,
        this.root
    )

class LightTreeSourceData(
    override val sourceIdentifier: SourceIdentifier,
    private val sourceCode: String,
    private val sourceOffset: Int,
    private val node: LighterASTNode
) : SourceData {
    override val indexRange: IntRange
        get() {
            val originalRange = node.range()
            val first = originalRange.first - sourceOffset
            val last = originalRange.last - sourceOffset
            return first..last
        }

    private
    val lineColumnInfo: LineColumnInfo
        get() = LineColumnInfo.fromIndexRange(sourceCode, sourceOffset, indexRange)
    override
    val lineRange: IntRange
        get() = lineColumnInfo.startLine..lineColumnInfo.endLine
    override
    val startColumn: Int
        get() = lineColumnInfo.startColumn
    override
    val endColumn: Int
        get() = lineColumnInfo.endColumn

    override
    fun text(): String = node.asText

    private
    class LineColumnInfo(val startLine: Int, val startColumn: Int, val endLine: Int, val endColumn: Int) {
        companion object Factory {
            fun fromIndexRange(text: String, offset: Int, offsetRelativeIndexRange: IntRange): LineColumnInfo {
                fun String.newLineLength(index: Int): Int =
                    when (this[index]) {
                        '\n' -> 1
                        '\r' -> {
                            if (index + 1 < length && this[index + 1] == '\n') 2 else 1
                        }
                        else -> 0
                    }

                fun String.isValidIndex(index: Int) = index in indices

                check(text.isValidIndex(offset))

                val realStartIndex = offset + offsetRelativeIndexRange.first
                check(text.isValidIndex(realStartIndex))

                val realEndIndex = offset + offsetRelativeIndexRange.last
                check(text.isValidIndex(realEndIndex))

                check(realStartIndex <= realEndIndex)

                var startLine = -1
                var startColumn = -1
                var endLine = -1
                var endColumn = -1

                var i = offset
                var line = 1
                var column = 1
                while (i < text.length) {
                    if (i == realStartIndex) {
                        startLine = line
                        startColumn = column
                    } else if (i == realEndIndex) {
                        endLine = line
                        endColumn = column
                        break
                    }

                    val newLineLength = text.newLineLength(i)
                    if (newLineLength > 0) {
                        i += newLineLength
                        line++
                        column = 1
                    } else {
                        i++
                        column++
                    }
                }

                check(startLine >= 0 && startColumn >= 0 && endLine >= 0 && endColumn >= 0)
                return LineColumnInfo(startLine, startColumn, endLine, endColumn)
            }
        }
    }
}


internal
fun FlyweightCapableTreeStructure<LighterASTNode>.print(
    node: LighterASTNode = root,
    indent: String = ""
) {
    val ref = Ref<Array<LighterASTNode?>>()

    getChildren(node, ref)
    val kidsArray = ref.get() ?: return

    for (kid in kidsArray) {
        if (kid == null) break
        kid.print(indent)
        print(kid, "\t$indent")
    }
}

internal
fun FlyweightCapableTreeStructure<LighterASTNode>.children(
    node: LighterASTNode
): List<LighterASTNode> {
    val ref = Ref<Array<LighterASTNode?>>()
    getChildren(node, ref)
    return ref.get()
        .filterNotNull()
        .filter { it.isUseful }
}

internal
fun FlyweightCapableTreeStructure<LighterASTNode>.getFirstChildExpressionUnwrapped(node: LighterASTNode): LighterASTNode? {
    val filter: (LighterASTNode) -> Boolean = { it -> it.isExpression() }
    val firstChild = firstChild(node, filter) ?: return null
    return if (firstChild.tokenType == PARENTHESIZED) {
        getFirstChildExpressionUnwrapped(firstChild)
    } else {
        firstChild
    }
}

internal
fun FlyweightCapableTreeStructure<LighterASTNode>.firstChild(
    node: LighterASTNode,
    filter: (LighterASTNode) -> Boolean
): LighterASTNode? {
    return children(node).firstOrNull(filter)
}

internal
val LighterASTNode.asText: String
    get() = this.toString()

internal
val LighterASTNode.isUseful: Boolean
    get() = !(COMMENTS.contains(tokenType) || tokenType == WHITE_SPACE || tokenType == SEMICOLON)

internal
fun LighterASTNode.expectKind(expected: IElementType) {
    check(isKind(expected))
}

internal
fun List<LighterASTNode>.expectSingleOfKind(expected: IElementType): LighterASTNode =
    this.single { it.isKind(expected) }

internal
fun LighterASTNode.isKind(expected: IElementType) =
    this.tokenType == expected

internal
fun LighterASTNode.sourceData(sourceIdentifier: SourceIdentifier, sourceCode: String, sourceOffset: Int) =
    LightTreeSourceData(sourceIdentifier, sourceCode, sourceOffset, this)

private
fun LighterASTNode.print(indent: String) {
    println("$indent${tokenType} (${range()}): ${content()}")
}

internal
fun LighterASTNode.range() = startOffset..endOffset

private
fun LighterASTNode.content(): String? =
    when (tokenType) {
        BLOCK -> ""
        SCRIPT -> ""
        FUN -> ""
        FUNCTION_LITERAL -> ""
        CALL_EXPRESSION -> ""
        LAMBDA_ARGUMENT -> ""
        LAMBDA_EXPRESSION -> ""
        WHITE_SPACE -> ""
        ERROR_ELEMENT -> PsiBuilderImpl.getErrorMessage(this)
        else -> toString()
    }