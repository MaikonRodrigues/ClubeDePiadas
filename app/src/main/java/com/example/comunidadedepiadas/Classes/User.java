package com.example.comunidadedepiadas.Classes;

public class User {
    String id, nome, email, data, avatar, senha;


    public User(String id, String nome, String email, String data, String avatar, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.data = data;
        this.avatar = avatar;
        this.senha = senha;
    }

    public User() {    }

    public String getAvatar() {
        return avatar;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }
}
