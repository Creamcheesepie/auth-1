package com.rest1.domain.post.comment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rest1.domain.member.member.entity.Member;
import com.rest1.domain.post.post.entity.Post;
import com.rest1.global.exception.ServiceException;
import com.rest1.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Comment extends BaseEntity {

    private String content;
    @ManyToOne
    @JsonIgnore
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    public Comment(Member author, String content , Post post) {
        this.author = author;
        this.content = content;
        this.post = post;
    }

    public void update(Member author,String content) {
        this.author = author;
        this.content = content;
    }

    public void isAuthorized(Member author){
        if(!this.author.equals(author)){
            throw new ServiceException("403-1","권한이 없는 사용자입니다.");
        }
    }
}
