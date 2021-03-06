package database.querys.cypherWrapper.cmu.pq;

import java.io.IOException;

import com.sun.javafx.scene.traversal.Direction;

import database.nodes.NodeTypes;
import database.querys.cypherWrapper.AbstractQuery;
import database.querys.cypherWrapper.Clause;
import database.querys.cypherWrapper.CompleteNode;
import database.querys.cypherWrapper.EdgeDirection;
import database.querys.cypherWrapper.EdgeImpl;
import database.querys.cypherWrapper.ExprImpl;
import database.querys.cypherWrapper.MatchClause;
import database.querys.cypherWrapper.MatchElement;
import database.querys.cypherWrapper.NodeVar;
import database.querys.cypherWrapper.ReturnClause;
import database.querys.cypherWrapper.WhereClause;
import database.querys.cypherWrapper.WithClause;
import database.querys.services.AssignmentServicesProgQueryImpl;
import database.relations.CDGRelationTypes;
import database.relations.RelationTypes;
import utils.dataTransferClasses.Pair;

public class OBJ54_SA extends AbstractQuery {
	/*
	 * private static final String OBJ54_DONT_TRY_HELP_GC_SETTING_REFS_TO_NULL =
	 * "MATCH
	 * (:LITERAL{typeKind:'NULL'})<-[:ASSIGNMENT_RHS]-(ass:ASSIGNMENT)<-[:
	 * MODIFIED_BY | STATE_MODIFIED_BY]-(localVar:VAR_DEC{typeKind:'DECLARED'})
	 * + " (ass)<-[" + assignToOutExprQuery + "*0..]-(expr)<-[" +
	 * exprToStatQuery + "]-(assignStat) " +
	 * 
	 * -[:USED_BY]->(use)<-[" + exprToOutExprQuery + "*]-(outExpr)<-[" +
	 * exprToStatQuery + "]-(useStat),"
	 * 
	 * " WITH COLLECT(useStat) AS useStats, localVar, assignStat " +
	 * " OPTIONAL MATCH (assignStat)-[" + cfgSuccesor + "*0..]->(useStat) " +
	 * " WHERE useStat IN useStats" +
	 * " WITH  localVar, COLLECT(DISTINCT useStat) AS reachableUseStats, assignStat "
	 * + " WHERE SIZE(reachableUseStats)=0" +
	 * " RETURN 'Warning [CMU-OBJ54] You must not try to help garbage collector setting references to null when they are no longer used. To make your code clearer, just delete the assignment in line ' + assignStat.lineNumber + ' of the varaible ' +localVar.name+ ' declared in line ' +localVar.lineNumber+'.'"
	 * ;
	 */
	public OBJ54_SA(boolean isProgQuery) {
		super(isProgQuery);
	}

	@Override
	protected void initiate() {
		clauses = new Clause[] {
				new MatchClause(
						((AssignmentServicesProgQueryImpl) getAssignmentServices())
								.getRightPartAssignmentsAndVarDeclarations(
										new CompleteNode("varDec"),
								new CompleteNode(NodeTypes.LITERAL, Pair.create("typeKind", "NULL"))),
						getExpressionServices().getStatementFromExp(new NodeVar("ass"), new NodeVar("assStat"))),
				new WhereClause("varDec:LOCAL_VAR_DEF OR varDec:PARAMETER_DEF"),
				getPDGServices().getOptionalUsesAndStateModsAndStatementsOf(new NodeVar("varDec")),
				new WithClause(new String[] { "varDec", "assStat" },
						Pair.create("useStats", new ExprImpl("COLLECT(stat)"))),
				new MatchClause(true, (MatchElement) getCFGServices().getCFGSuccesorsOf(new NodeVar("assStat"))),
				new WhereClause("succ IN useStats"),
				new WithClause(new String[] { "varDec", "assStat", "useStats" },
						Pair.create("numberOfReachableUseStats", new ExprImpl("COUNT(succ)"))),
				new WhereClause("numberOfReachableUseStats=0"),
				new MatchClause(false, getStatementServices().getEnclosingClassFromDeclaration(new NodeVar("varDec")).append( Pair.create(new EdgeImpl(EdgeDirection.OUTGOING, RelationTypes.HAS_TYPE_DEF, CDGRelationTypes.HAS_INNER_TYPE_DEF), new NodeVar("cu")))),
				new ReturnClause(
						// "varDec, assStat, useStats, enclClass"
						"'[CMU-OBJ54;'+cu.fileName+';expression;ASSIGNMENT;'+assStat.lineNumber+'; You must not try to help garbage collector setting references to null when they are no longer used. To make your code clearer, just delete the assignment in line ' + assStat.lineNumber + ' of the variable ' +varDec.name+ ' declared in line '+varDec.lineNumber+ ',class '+enclClass.fullyQualifiedName+'.]'"
						)
				};
	}
	//[BLOCH-6.36;C:\Users\admp1\Escritorio\StaticCodeAnalysis\Programs\ExampleClasses\src\main\java\examples\test\rule_3_10\B1.java;
	//method_definition;examples.test.rule_3_10.B1:toString()java.lang.String;10;You must use the Override annotation in method toString (line 10) since it is actually overriding the method java.lang.Object:toString()java.lang.String]

	public static void main(String[] args) throws IOException {
		System.out.println(new OBJ54_SA(true).queryToString());
	}
}
