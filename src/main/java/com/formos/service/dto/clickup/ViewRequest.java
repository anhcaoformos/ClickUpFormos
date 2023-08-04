package com.formos.service.dto.clickup;

public class ViewRequest {

    private String name;
    private Parent parent;
    private int type;

    public ViewRequest(String name, Parent parent, int type) {
        this.name = name;
        this.parent = parent;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static class Parent {

        private String id;
        private int type;

        public Parent(String id, int type) {
            this.id = id;
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
