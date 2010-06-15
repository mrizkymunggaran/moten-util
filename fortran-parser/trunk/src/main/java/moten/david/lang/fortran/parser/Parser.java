package moten.david.lang.fortran.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import moten.david.util.io.ReaderEnumeration;

import org.apache.commons.lang.math.NumberUtils;

import com.google.common.collect.Lists;

public class Parser {

    private final Set<String> tokens = new TreeSet<String>();

    public Set<String> getTokens() {
        return tokens;
    }

    public List<List<String>> read(final InputStream is) {

        tokens.clear();
        List<List<String>> lines = Lists.newArrayList();
        ReaderEnumeration en = new ReaderEnumeration(is);
        List<String> lastList = null;
        while (en.hasMoreElements()) {
            String line = en.nextElement();
            // System.out.println(line);
            if (line.length() > 0 && !line.startsWith("C")
                    && !line.startsWith("c")) {
                StringTokenizer s = new StringTokenizer(line,
                        "(),. ='-+*/ :&#", true);
                List<String> list = Lists.newArrayList();
                boolean stringOpen = false;
                String stringDelimiter = null;
                StringBuffer sb = null;
                while (s.hasMoreTokens()) {
                    String token = s.nextToken();
                    if (("'".equals(token) || "\"".equals(token))
                            && !stringOpen) {
                        stringOpen = true;
                        sb = new StringBuffer();
                        stringDelimiter = token;
                        sb.append(token);
                    } else if (stringOpen && stringDelimiter.equals(token)) {
                        // stringOpen is true
                        stringOpen = false;
                        sb.append(token);
                        list.add(sb.toString());
                    } else if (stringOpen)
                        sb.append(token);
                    // only add non repeated spaces
                    else if (!" ".equals(token))
                        list.add(token);
                }
                if (list.size() > 0 && "#".equals(list.get(0)))
                    if (lastList == null)
                        throw new RuntimeException(
                                "line started with continuation character # but is the first line in the file!");
                    else {
                        lastList.addAll(list.subList(1, list.size()));
                        list = null;
                    }
                else {
                    // System.out.println(list);
                    lines.add(list);
                    lastList = list;
                }
                if (list != null)
                    for (String token : list)
                        if (!"=".equals(token) && !token.startsWith("'")
                                && !token.startsWith("\"")
                                && !NumberUtils.isNumber(token))
                            tokens.add(token);
            }
        }
        return lines;
    }

    public Node getNodes(List<List<String>> lines) {
        Node root = null;
        Node current = null;
        for (List<String> line : lines) {

            Node node = null;
            if (is(line, 0, "program")) {
                node = new Program(null, value(line, 1));
                root = node;
                current = node;
            } else if (is(line, 0, "subroutine")) {
                node = new Subroutine(current, value(line, 1));
                current.add(node);
                current = node;
            } else if (is(line, 0, "function")) {
                node = new Function(current, value(line, 1));
                current.add(node);
                current = node;
            } else if (is(line, 0, "end")) {
                if (current.getParent() != null)
                    current = current.getParent();
            } else if (is(line, 0, "call")) {
                node = new Call(current, value(line, 1),
                        getExpressions(getSubList(line, 2)));
                current.add(node);
            } else if (is(line, 0, "stop")) {
                node = new Stop(current, value(line, 1));
                current.add(node);
            } else if (is(line, 0, "return")) {
                node = new Return(current);
                current.add(node);
            } else if (is(line, 0, "continue")) {
                node = new Continue(current);
                current.add(node);
            } else if (is(line, 0, "if")) {
                int j = getIndexPastExpression(line, 2);
                if (is(line, j, "then")) {
                    List<String> e = Lists.newArrayList();
                    for (int k = 2; k <= j - 2; k++) {
                        e.add(value(line, k));
                    }
                    BooleanExpression expression = getBooleanExpression(e);
                    node = new If(current, expression);
                    current.add(node);
                    current = node;
                } else {
                    // TODO
                }
            } else if (is(line, 0, "do")) {
                int i = 0;
                Integer toLabel = null;
                if (NumberUtils.isDigits(value(line, 1))) {
                    toLabel = Integer.parseInt(value(line, 1));
                    i++;
                }
                Variable variable = new Variable(value(line, i + 1));
                Expression from = new Expression(value(line, i + 3));
                Expression to = new Expression(value(line, i + 5));
                Expression increment = null;
                if (value(line, i + 7) != null)
                    increment = new Expression(value(line, i + 7));
                node = new Do(current, toLabel, variable, from, to, increment);
                current.add(node);
            } else if (is(line, 1, "=")) {
                Variable variable = new Variable(value(line, 0));
                Expression expression = new Expression("bubub");
                node = new Assignment(current, variable, expression);
                current.add(node);
            } else if (is(line, 1, "(")) {
                String name = value(line, 0);
                int i = getIndexPastExpression(line, 1);

                for (int j = 2; j <= i - 2; j++) {

                }
            }
            String label = getLabel(line);
            if (node != null && label != null)
                node.setLabel(label);

        }
        return root;
    }

    /**
     * @param list
     * @param startIndex
     *            index of first open bracket
     * @return
     */
    private int getIndexPastExpression(List<String> list, int startIndex) {
        int j = startIndex + 1;// index of first open bracket
        int count = 1;
        while (count > 0) {
            if (")".equals(value(list, j)))
                count--;
            j++;
        }
        return j;
    }

    private BooleanExpression getBooleanExpression(List<String> e) {
        StringBuffer s = new StringBuffer();
        for (String word : e)
            s.append(word);
        return new BooleanExpression(s.toString());
    }

    private Expression getExpression(List<String> e) {
        // TODO Auto-generated method stub
        return null;
    }

    private List<String> getSubList(List<String> list, int i) {
        if (list.size() <= i)
            return Lists.newArrayList();
        else if (NumberUtils.isDigits(list.get(0)))
            if (list.size() <= i + 1)
                return Lists.newArrayList();
            else
                return list.subList(i + 1, list.size());
        else if (list.size() > 2 && ":".equals(list.get(1)))
            if (list.size() <= i + 2)
                return Lists.newArrayList();
            else
                return list.subList(i + 2, list.size());
        else
            return list.subList(i, list.size());
    }

    private Expression[] getExpressions(List<String> list) {
        // may or may not start and end with ()
        // TODO implement

        if (list.size() > 0 && "(".equals(list.get(0)))
            return getExpressions(list.subList(1, list.size() - 1));
        else {
            List<Expression> expressions = Lists.newArrayList();
            StringBuffer s = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                if (",".equals(list.get(i))) {
                    Expression expression = new Expression(s.toString());
                    expressions.add(expression);
                    s = new StringBuffer();
                } else {
                    s.append(list.get(i));
                }
            }
            if (s.length() > 0) {
                Expression expression = new Expression(s.toString());
                expressions.add(expression);
            }
            return expressions.toArray(new Expression[] {});
        }

    }

    private String getLabel(List<String> list) {
        if (list.size() > 0 && NumberUtils.isDigits(list.get(0)))
            return list.get(0);
        else if (list.size() > 2 && ":".equals(list.get(1)))
            return list.get(0);
        else
            return null;
    }

    private boolean is(List<String> list, int i, String value) {
        return value.equalsIgnoreCase(value(list, i));
    }

    /**
     * Skips line numbers and label indicators.
     * 
     * @param list
     * @param i
     * @return
     */
    private String value(List<String> list, int i) {
        if (list.size() <= i)
            return null;
        else if (NumberUtils.isDigits(list.get(0)))
            if (list.size() <= i + 1)
                return null;
            else
                return list.get(i + 1);
        else if (list.size() > 2 && ":".equals(list.get(1)))
            if (list.size() <= i + 2)
                return null;
            else
                return list.get(i + 2);
        else
            return list.get(i);
    }

    private static void print(PrintStream out, Node node, String indent) {
        String start = indent;
        if (node.getLabel() != null) {
            start = node.getLabel() + " ";
            if (indent.length() > start.length())
                start += indent.substring(start.length());
        }
        out.println(start + node);
        for (Node child : node.getChildren())
            print(out, child, indent + "   ");
    }

    public static void main(String[] args) throws FileNotFoundException {
        Parser parser = new Parser();
        List<List<String>> lines = parser.read(new FileInputStream(
                "../SeaSAR/src/fortran/gems/gcom3d.for"));
        Node node = parser.getNodes(lines);

        print(System.out, node, "");
        // System.out.println(parser.getTokens());
    }
}
