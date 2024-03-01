package com.group06.bsms.books;

public interface TableActionEvent {

    public void setIsHiddenBtn(boolean isHiddenBtn);

    public boolean isIsHiddenBtn();

    public void onEdit(int row);

    public void onHide(int row);
}
