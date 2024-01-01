package view;

import javafx.scene.control.TreeCell;
import javafx.scene.text.Font;

//修改文件树字体大小
public class CustomTreeCell extends TreeCell<String> {

    private static final int CUSTOM_FONT_SIZE = 20;  //设置文件树字体大小

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
        } else {
            setText(item);
            setFont(new Font(CUSTOM_FONT_SIZE));
        }
    }
}
