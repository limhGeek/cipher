package com.limh.cipher.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Function:
 * author: limh
 * time:2017/12/5
 */
@Entity
public class Note {
    @Id
    private Long id;
    private String group;
    private String title;
    private String username;
    private String password;
    private String content;
    private String createAt;
    private String updateAt;
    @Generated(hash = 1151883986)
    public Note(Long id, String group, String title, String username,
            String password, String content, String createAt, String updateAt) {
        this.id = id;
        this.group = group;
        this.title = title;
        this.username = username;
        this.password = password;
        this.content = content;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }
    @Generated(hash = 1272611929)
    public Note() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getGroup() {
        return this.group;
    }
    public void setGroup(String group) {
        this.group = group;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getCreateAt() {
        return this.createAt;
    }
    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
    public String getUpdateAt() {
        return this.updateAt;
    }
    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "Note{" + "id=" + id +
                ", group='" + group + '\'' +
                ", title='" + title + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", content='" + content + '\'' +
                ", createAt='" + createAt + '\'' +
                ", updateAt='" + updateAt + '\'' +
                '}';
    }
}
