/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.jbehave.core.story.codegen.sablecc.node;

import org.jbehave.core.story.codegen.sablecc.analysis.*;

public final class TPackageKeyword extends Token
{
    public TPackageKeyword()
    {
        super.setText("Package:");
    }

    public TPackageKeyword(int line, int pos)
    {
        super.setText("Package:");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TPackageKeyword(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTPackageKeyword(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TPackageKeyword text.");
    }
}