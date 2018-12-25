package com.ttruyen.model;

public class Story {
    private int id;
    private String name;
    private String url;
    private String description;
    private String image;
    private int status;

    private Story(StoryBuild storyBuild) {
        this.id = storyBuild.id;
        this.name = storyBuild.name;
        this.url = storyBuild.url;
        this.description = storyBuild.description;
        this.image = storyBuild.image;
        this.status = storyBuild.status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public int getStatus() {
        return status;
    }

    public static class StoryBuild {
        private int id;
        private String name;
        private String url;
        private String description;
        private String image;
        private int status;

        public StoryBuild(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public StoryBuild widthId(int id) {
            this.id = id;
            return this;
        }

        public StoryBuild widthDescription(String description) {
            this.description = description;
            return this;
        }

        public StoryBuild widthImage(String image) {
            this.image = image;
            return this;
        }

        public StoryBuild widthStatus(int status) {
            this.status = status;
            return this;
        }

        public Story build() {
            return new Story(this);
        }
    }
}
