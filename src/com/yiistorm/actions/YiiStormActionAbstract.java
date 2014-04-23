package com.yiistorm.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 22.02.13
 * Time: 20:48
 * To change this template use File | Settings | File Templates.
 */
abstract public class YiiStormActionAbstract extends AnAction implements IAction {
    protected String _code;
    protected int _cursorOffset = -1;
    protected AnActionEvent _event;
    protected DataContext _dataContext;
    protected Project _project;
    protected Editor _editor;
    protected VirtualFile _virtualFile;
    protected PsiFile _psiFile;
    protected DocumentImpl _document;
    protected CaretModel _caretModel;

    private void reset() {
        _event = null;
        _caretModel = null;
        _code = null;
        _cursorOffset = -1;
        _dataContext = null;
        _document = null;
        _editor = null;
        _project = null;
        _psiFile = null;
        _virtualFile = null;
    }

    public void setEvent(AnActionEvent _event) {
        reset();
        this._event = _event;
    }

    public AnActionEvent getEvent() {
        return _event;
    }

    public DataContext getDataContext() {
        if (_dataContext == null) {
            if (getEvent() != null) {
                _dataContext = getEvent().getDataContext();
            }
        }
        return _dataContext;
    }

    public Project getProject() {
        if (_project == null) {
            if (getDataContext() != null) {
                _project = PlatformDataKeys.PROJECT.getData(getDataContext());
            }
        }
        return _project;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        setEvent(e);
        executeAction();
    }

    public abstract void executeAction();
}
