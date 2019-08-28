package com.example.comunidadedepiadas.Classes;

public class Piada {
    String descriscao,  user_id, categoria_id;
    String id, likes, dslikes;

    public Piada(String descriscao, String id, String likes, String dslikes, String user_id) {
        this.descriscao = descriscao;
        this.id = id;
        this.likes = likes;
        this.dslikes = dslikes;
        this.user_id = user_id;
    }

    public Piada() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDescriscao() {
        return descriscao;
    }

    public void setDescriscao(String descriscao) {
        this.descriscao = descriscao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDslikes() {
        return dslikes;
    }

    public void setDslikes(String dslikes) {
        this.dslikes = dslikes;
    }

    public String getCategoria_id() {
        return categoria_id;
    }

    public void setCategoria_id(String categoria_id) {
        this.categoria_id = categoria_id;
    }
}
