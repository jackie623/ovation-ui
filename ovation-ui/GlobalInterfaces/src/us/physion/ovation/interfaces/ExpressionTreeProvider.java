package us.physion.ovation.interfaces;

import com.physion.ebuilder.expression.ExpressionTree;


public interface ExpressionTreeProvider {

    public ExpressionTree getExpressionTree();
    public void addQueryListener(QueryListener cl);
    public void removeQueryListener(QueryListener cl);

}
