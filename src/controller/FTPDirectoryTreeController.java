package controller;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.net.ftp.FTPClient;
import view.CustomTreeCell;

import ftp.FTPManager;
import javafx.fxml.FXML;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.util.LinkedList;
import java.util.Optional;


public class FTPDirectoryTreeController {

    //ftpTreeView组件
    @FXML
    private TreeView<String> ftpTreeView;

    //private FTPManager ftpManager = new FTPManager();

    @FXML
    private ImageView exitButton;  //退出按钮

    //初始化
    //在加载关联的FXML文件时会自动调用
    @FXML
    void initialize() {
        ftpTreeView.setCellFactory(treeView -> new CustomTreeCell());  //修改文件树字体大小

        ftpTreeView.setRoot(new TreeItem<>("FTP Server"));  //设置根节点

        ftpTreeView.setShowRoot(false);  //不显示根节点

        //为ftpTreeView设置一个鼠标点击事件处理器
        //当用户点击ftpTreeView中的节点时，会触发这个处理器
        //this::handleTreeViewClick是一个方法引用
        //当鼠标点击事件发生时，handleTreeViewClick方法将被调用
        ftpTreeView.setOnMouseClicked(this::handleTreeViewClick);  //显示结点的下拉菜单

        try {
            populateTreeView("/", ftpTreeView.getRoot());  //找到var文件夹
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void populateTreeView(String currentPath, TreeItem<String> parentItem) {  //找到var文件夹
        try {

            FTPFile[] files = FTPManager.ftpManager.listFiles(currentPath);  //拿到FTP根目录下的所有文件名

            if (files != null) {
                for (FTPFile file : files) {  //遍历所有文件名
                    System.out.println("此时遍历的文件夹名：" + file.getName());

                    if (file.isDirectory() && file.getName().contains("var")) {  //如果文件名为var
                        TreeItem<String> newItem = new TreeItem<>(file.getName());  //文件树生成子结点
                        parentItem.getChildren().add(newItem);  //添加子结点

                        populateSubdirectories(currentPath + file.getName() + "/", newItem);  //调用populateSubdirectories在var文件夹下找ftp文件夹
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateSubdirectories(String currentPath, TreeItem<String> parentItem) {  // 找"/var/ftp/"
        try {
            FTPFile[] files = FTPManager.ftpManager.listFiles(currentPath);  //拿到var文件夹下的所有文件名

            if (files != null) {
                for (FTPFile file : files) {
                    System.out.println("此时遍历的文件夹名：" + file.getName());
                    if (file.isDirectory() && file.getName().contains("ftp")) {  //找到ftp文件夹
                        TreeItem<String> newItem = new TreeItem<>(file.getName());  //生成子节点
                        parentItem.getChildren().add(newItem);  //添加子节点
                        populateSubdirectories1(currentPath + file.getName() + "/", newItem);  //调用populateSubdirectories1生成/var/ftp/下所有目录及文件的文件树
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //生成/var/ftp/下所有目录及文件的文件树
    private void populateSubdirectories1(String currentPath, TreeItem<String> parentItem) {
        try {
            FTPFile[] files = FTPManager.ftpManager.listFiles(currentPath);  //获得/var/ftp/下的所有文件或文件夹

            if (files != null) {
                for (FTPFile file : files) {
                    TreeItem<String> newItem = new TreeItem<>(file.getName());  //生成子节点
                    if (file.isDirectory()) {
                        parentItem.getChildren().add(newItem);  //是文件夹要添加成子节点
                        populateSubdirectories1(currentPath + file.getName() + "/", newItem);  //递归子节点
                    } else {
                        parentItem.getChildren().add(new TreeItem<>(file.getName()));  //不是文件夹直接添加同级节点
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //点击退出按钮
    //在加载关联的FXML文件时会自动调用
    @FXML
    void mouseClickexitButton(MouseEvent event) throws Exception {
        FTPManager.ftpManager.disconnect();  //注销
        System.exit(0);  //退出系统
    }

    //鼠标进入 退出按钮 时更改不透明度
    @FXML
    void mouseEnterexitButton(MouseEvent event) {
        exitButton.setOpacity(0.8);
    }

    //鼠标退出 退出按钮 时更改不透明度
    @FXML
    void mouseExitexitButton(MouseEvent event) {
        exitButton.setOpacity(1);
    }

    //显示下拉菜单
    //接收参数为MouseEvent，用于处理鼠标点击事件
    private void handleTreeViewClick(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {  //MouseButton.SECONDARY是鼠标右键
            TreeItem<String> selectedItem = ftpTreeView.getSelectionModel().getSelectedItem();  //获取当前选中的树节点保存在selectedItem变量中
            if (selectedItem != null) {
                ContextMenu contextMenu = createContextMenu(selectedItem);  //为选中的节点创建右键菜单
                contextMenu.show(ftpTreeView, event.getScreenX(), event.getScreenY());  //显示创建菜单，附着在ftpTreeView上，并在鼠标点击的屏幕坐标位置显示
            }
        }
    }

    //填充下拉菜单内容
    private ContextMenu createContextMenu(TreeItem<String> selectedItem) {
        ContextMenu contextMenu = new ContextMenu();

        //如果是文本文件
        if (isTextFile(selectedItem.getValue())) {
            MenuItem downloadTextFileItem = new MenuItem("下载文本文件");
            downloadTextFileItem.setOnAction(event -> {  //设置事件
                try {
                    handleDownloadTextFile(selectedItem);  //下载文本文件
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            MenuItem deleteFolderItem = new MenuItem("删除文本文件");
            deleteFolderItem.setOnAction(event -> handleDeleteFolder(selectedItem));

            contextMenu.getItems().addAll(downloadTextFileItem,deleteFolderItem);
        } else if (isImageFile(selectedItem.getValue())) {
            MenuItem downloadImageFileItem = new MenuItem("下载图片");
            downloadImageFileItem.setOnAction(event -> {
                try {
                    handleDownloadImageFile(selectedItem);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            MenuItem deleteFolderItem = new MenuItem("删除图片");
            deleteFolderItem.setOnAction(event -> handleDeleteFolder(selectedItem));
            contextMenu.getItems().addAll(downloadImageFileItem,deleteFolderItem);
        } else {
            MenuItem newFolderItem = new MenuItem("新建目录/文件夹");
            newFolderItem.setOnAction(event -> handleNewFolder(selectedItem));

            MenuItem deleteFolderItem = new MenuItem("删除目录/文件夹");
            deleteFolderItem.setOnAction(event -> handleDeleteFolder(selectedItem));

            MenuItem uploadTextFileItem = new MenuItem("上传文本文件");
            uploadTextFileItem.setOnAction(event -> handleUploadTextFile(selectedItem));

            MenuItem uploadImageFileItem = new MenuItem("上传图片");
            uploadImageFileItem.setOnAction(event -> handleUploadImageFile(selectedItem));

            contextMenu.getItems().addAll(newFolderItem, deleteFolderItem, uploadTextFileItem, uploadImageFileItem);
        }

//        contextMenu.setOnHidden(event -> {
//            // Implement logic for when the context menu is hidden
//            System.out.println("Context menu hidden");
//        });

        return contextMenu;
    }

    //判断是否为图片
    private boolean isImageFile(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.endsWith(".png") || lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg");
    }

    //判断是否为文本文件
    private boolean isTextFile(String fileName) {
        return fileName.toLowerCase().endsWith(".txt");
    }

    //获取当前完整路径
    private String getCurrentPath(TreeItem<String> selectedItem) {
        StringBuilder pathBuilder = new StringBuilder("/");
        LinkedList<String> pathComponents = new LinkedList<>();
        TreeItem<String> currentItem = selectedItem;  //当前节点

        while (currentItem != null && !currentItem.getValue().equals("FTP Server")) {  //当前节点不为空且不是根节点，即从叶子节点到根节点的遍历
            pathComponents.addFirst(currentItem.getValue());  //每向上遍历一级，将该级的值添加到pathComponents列表的开头
            //pathBuilder.append(currentItem.getValue()).append("/");
            currentItem = currentItem.getParent();
        }
        for (String component : pathComponents) {
            pathBuilder.append(component).append("/");
        }
        System.out.println("原始完整目录：" + pathBuilder.toString());
        return pathBuilder.toString();
    }

    //输入新建文件夹名
    private String promptForNewFolderName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("新建文件夹");
        dialog.setHeaderText("输入新建的文件夹名:");
        dialog.setContentText("文件夹名:");

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/云朵图标.png"));

        Optional<String> result = dialog.showAndWait();  //等待用户输入
        return result.orElse(null);  //返回用户输入的值
        //如果用户输入了一个值，那么这个值将被返回；如果用户没有输入任何值（即对话框被关闭但没有输入任何内容），那么将返回null
    }


    //点击新建文件夹
    private void handleNewFolder(TreeItem<String> selectedItem) {
        try {
            //原始完整目录
            String currentPath = getCurrentPath(selectedItem);

            //新建文件夹名
            String newFolderName = promptForNewFolderName();

            if (newFolderName != null && !newFolderName.isEmpty()) {

                //创建目录完整路径
                String newFolderPath = currentPath + newFolderName + "/";

                //如果目录不存在，则创建
                if (!FTPManager.client.changeWorkingDirectory(new String(newFolderPath.getBytes("utf-8"), "ISO-8859-1"))) {

                    //创建目录
                    FTPManager.client.makeDirectory(new String(newFolderPath.getBytes("utf-8"), "ISO-8859-1"));
                    System.out.println("创建目录成功: " + new String(newFolderPath.getBytes("utf-8"), "ISO-8859-1"));

                    //刷新文件树
                    refreshTreeView(selectedItem);
                } else {
                    System.out.println("该目录已存在： " + newFolderPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //刷新文件树
    private void refreshTreeView(TreeItem<String> selectedItem) {
        selectedItem.getChildren().clear();  //清空所有子节点
        populateSubdirectories1(getCurrentPath(selectedItem), selectedItem);  //重新遍历递归子节点
    }

    //点击删除文件夹
    private void handleDeleteFolder(TreeItem<String> selectedItem) {

        String itemName = selectedItem.getValue();  //获得当前文件夹或文件名
        //String currentPath = getCurrentPath(selectedItem);
        //boolean isFile = selectedItem.getChildren().isEmpty();  //子节点为空的文件或文件夹
        //boolean isFile = selectedItem.getChildren().isEmpty();  //子节点为空的文件或文件夹
        //区分文件和文件夹
        boolean isFile = itemName.contains(".");  //文件名包含.后缀

        boolean isFilefolder = !itemName.contains(".");  //文件夹名不包含.

        //确认删除弹窗
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("删除目录/文件");
        alert.setHeaderText(null);
        alert.setContentText("认定删除" + itemName + "?");

        //设置弹窗图标
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/云朵图标.png"));

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                //是文件
                if (isFile) {
                    String currentPath = getCurrentPath(selectedItem.getParent());  //获得完整路径，不包含文件
                    deleteFile(currentPath, itemName);  //删除文件，传入删除的路径和文件名
                    selectedItem.getParent().getChildren().remove(selectedItem);  //在文件树上删除对应节点
                    //System.out.println("Deleted:" + itemName);
                } else if (isFilefolder) {  //是文件夹
                    String currentPath = getCurrentPath(selectedItem);  //获取当前文件夹的完整目录
                    //System.out.println("删除文件夹："+currentPath);
                    deleteFilefolder(currentPath, selectedItem);//把当前路径和树节点传给文件夹删除函数
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error deleting: " + itemName);
            }
        }

    }

    //删除非文件夹，传入的参数为文件的完整路径和文件名
    private void deleteFile(String filePath, String fileName) throws Exception {

        System.out.println("被删除文件的完整路径：" + filePath + "被删除的文件名" + fileName);

        if (FTPManager.client.changeWorkingDirectory(filePath)) {  //切换到被删除文件的完整路径

            FTPFile[] files = FTPManager.client.listFiles();  //获取路径下的所有文件

            for (FTPFile file : files) {  //遍历所有文件
                System.out.println("当前文件名：" + file);
                if (file.getName().equals(fileName)) {  //找到要删除的文件
                    System.out.println("已找到被删除的文件：" + file.getName());
                    boolean flag = FTPManager.client.deleteFile(new String(file.getName().getBytes("utf-8"), "ISO-8859-1"));  //删除文件
                    System.out.println(flag ? "文件删除成功！" : "文件删除失败！");
                    break;
                }
            }
        }
    }

    // 文件夹删除函数
    private void deleteFilefolder(String folderPath, TreeItem<String> selectedItem) throws Exception {
        try {
            System.out.println("文件夹删除函数开始时路径：" + folderPath);
            System.out.println("文件夹删除函数开始时节点：" + selectedItem.getValue());

            FTPFile[] files = FTPManager.client.listFiles(folderPath);  //拿到要删除的文件夹下的所有文件或文件夹

            //如果当前文件夹不为空,其子目录可能为空文件夹
            if (files != null && files.length > 0) {

                for (FTPFile file : files) {

                    System.out.println("文件夹删除函数当前FTPFile file" + file.getName());

                    if (file.isDirectory()) {
                        //是文件夹
                        String fileName = file.getName();
                        String filePath = folderPath + file.getName() + "/";
                        TreeItem<String> childNode = findChildNode(selectedItem, fileName);  //找到该fileName的子节点
                        deleteFilefolder(filePath, childNode);  //递归删除子节点
                    } else if (file.isFile()) {
                        //非文件夹
                        String fileName = file.getName();
                        deleteFile(folderPath, fileName);  //删除文件
                    }
                }
            }

            //当前文件夹为空，删除文件夹
            boolean removed = FTPManager.client.removeDirectory(folderPath);

            if (removed) {
                //selectedItem.getParent().getChildren().remove(selectedItem);  //删除节点
                System.out.println("如果删除了文件夹,当前节点" + selectedItem.getValue());
                TreeItem<String> parent = selectedItem.getParent();
                System.out.println("如果删除了文件夹,当前节点父节点" + parent.getValue());
                if (parent != null) {
                    parent.getChildren().remove(selectedItem);
                } else {
                    System.out.println("Parent is null. Cannot remove item.");
                }

            } else {
                System.out.println("Failed to remove directory: " + folderPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //找到子节点
    private TreeItem<String> findChildNode(TreeItem<String> parent, String value) {
        for (TreeItem<String> child : parent.getChildren()) {
            if (child.getValue().equals(value)) {
                return child;
            }
        }
        return null;
    }

    //点击上传文本文件
    private void handleUploadTextFile(TreeItem<String> selectedItem) {
        try {
            FTPManager.client.setFileType(FTPClient.ASCII_FILE_TYPE);  //传输文本文件

            String currentPath = getCurrentPath(selectedItem);  //得到当前完整路径

            System.out.println("当前完整路径currentPath" + currentPath);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择要上传的文本文件");

            //为文件选择器FileChooser添加一个扩展名过滤器ExtensionFilter
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            //弹出一个文件选择对话框并将所选文件的路径保存在selectedFile中
            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile != null) {

                if (!FTPManager.client.changeWorkingDirectory(currentPath)) {
                    FTPManager.client.makeDirectory(currentPath);
                }

                String localFilePath = selectedFile.getAbsolutePath();  //获得文本文件的绝对路径
                System.out.println("文本文件的绝对路径localFilePath:" + localFilePath);

                String tempName = selectedFile.getName();
                String ftpFileName = new String(tempName.getBytes("utf-8"), "ISO-8859-1");  //解决文件名乱码
                System.out.println("文件名：" + ftpFileName);

                FileInputStream fileInputStream = new FileInputStream(localFilePath);  //打开文件输入流

                if (FTPManager.client.storeFile(ftpFileName, fileInputStream)) {  //上传文本文件
                    System.out.println("FTP文本文件上传成功！");
                    refreshTreeView(selectedItem);  //刷新文件树
                } else {
                    System.out.println("FTP文本文件上传失败！");
                }
                fileInputStream.close();  //关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //点击下载文本文件
    private void handleDownloadTextFile(TreeItem<String> selectedItem) throws IOException {

        FTPManager.client.setFileType(FTPClient.ASCII_FILE_TYPE);  //传输文本文件

        //确认下载弹窗
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("下载文本文件");
        alert.setHeaderText("默认下载路径为E盘");
        alert.setContentText("确认下载" + selectedItem.getValue() + "?");

        //设置弹窗图标
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/云朵图标.png"));

        //确认下载
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                String currentPath = getCurrentPath(selectedItem.getParent());  //获得要下载的文件的完整目录
                System.out.println("获得要下载的文件的完整目录currentPath:" + currentPath);

                String fileName = selectedItem.getValue();  //要下载的文件名称

                if (FTPManager.client.changeWorkingDirectory(currentPath)) {  //切换路径

                    FTPFile[] files = FTPManager.client.listFiles();  //获得目录下所有的文件或文件夹

                    for (FTPFile file : files) {
                        if (file.getName().equalsIgnoreCase(fileName)) {  //找到要下载的文本文件

                            File localFile = new File("e:" + File.separator + "download_" + fileName);  //下载到本地路径

                            OutputStream os = new FileOutputStream(localFile);  //打开输出流

                            boolean flag = FTPManager.client.retrieveFile(new String(file.getName().getBytes("utf-8"), "ISO-8859-1"), os);

                            os.flush();  //刷新输出流

                            os.close();  //关闭输出流

                            System.out.println(flag ? "文件下载成功！" : "文件下载失败！");

                            break;
                        }
                    }
                } else {
                    System.out.println("切换" + currentPath + "路径失败！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    //点击上传图片
    private void handleUploadImageFile(TreeItem<String> selectedItem) {
        try {
            FTPManager.client.setFileType(FTPClient.BINARY_FILE_TYPE);  //传输图片
            String currentPath = getCurrentPath(selectedItem);  //得到当前完整路径
            System.out.println("当前完整路径currentPath" + currentPath);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择要上传的图片");

            FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG Files", "*.png");
            FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("JPEG Files", "*.jpg");
            FileChooser.ExtensionFilter jpegFilter = new FileChooser.ExtensionFilter("JPEG Files", "*.jpeg");

            fileChooser.getExtensionFilters().addAll(pngFilter, jpgFilter, jpegFilter);


            //弹出一个文件选择对话框并将所选文件的路径保存在selectedFile中
            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile != null) {

                if (!FTPManager.client.changeWorkingDirectory(currentPath)) {
                    FTPManager.client.makeDirectory(currentPath);
                }

                String localFilePath = selectedFile.getAbsolutePath();  //获得文本文件的绝对路径
                System.out.println("文本文件的绝对路径localFilePath:" + localFilePath);

                String tempName = selectedFile.getName();
                String ftpFileName = new String(tempName.getBytes("utf-8"), "ISO-8859-1");  //解决文件名乱码
                System.out.println("文件名：" + ftpFileName);

                FileInputStream fileInputStream = new FileInputStream(localFilePath);  //打开文件输入流

                if (FTPManager.client.storeFile(ftpFileName, fileInputStream)) {  //上传文本文件
                    System.out.println("FTP图片上传成功！");
                    refreshTreeView(selectedItem);  //刷新文件树
                } else {
                    System.out.println("FTP图片上传失败！");
                }
                fileInputStream.close();  //关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //点击下载图片
    private void handleDownloadImageFile(TreeItem<String> selectedItem) throws IOException {

        FTPManager.client.setFileType(FTPClient.BINARY_FILE_TYPE);  //传输图片
        //确认下载弹窗
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("下载图片");
        alert.setHeaderText("默认下载路径为E盘");
        alert.setContentText("确认下载" + selectedItem.getValue() + "?");

        //设置弹窗图标
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/云朵图标.png"));

        //确认下载
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                String currentPath = getCurrentPath(selectedItem.getParent());  //获得要下载的文件的完整目录
                System.out.println("获得要下载的文件的完整目录currentPath:" + currentPath);

                String fileName = selectedItem.getValue();  //要下载的文件名称

                if (FTPManager.client.changeWorkingDirectory(currentPath)) {  //切换路径

                    FTPFile[] files = FTPManager.client.listFiles();  //获得目录下所有的文件或文件夹

                    for (FTPFile file : files) {
                        if (file.getName().equalsIgnoreCase(fileName)) {  //找到要下载的文本文件

                            File localFile = new File("e:" + File.separator + "download_" + fileName);  //下载到本地路径

                            OutputStream os = new FileOutputStream(localFile);  //打开输出流

                            boolean flag = FTPManager.client.retrieveFile(new String(file.getName().getBytes("utf-8"), "ISO-8859-1"), os);

                            os.flush();  //刷新输出流

                            os.close();  //关闭输出流

                            System.out.println(flag ? "图片下载成功！" : "图片下载失败！");

                            break;
                        }
                    }
                } else {
                    System.out.println("切换" + currentPath + "路径失败！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
