package com.app.redditclone.services;

import com.app.redditclone.dto.VoteDto;
import com.app.redditclone.exceptions.PostNotFoundException;
import com.app.redditclone.exceptions.SpringRedditException;
import com.app.redditclone.model.Post;
import com.app.redditclone.model.Vote;
import com.app.redditclone.model.VoteType;
import com.app.redditclone.repository.PostRepository;
import com.app.redditclone.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    @Transactional
    public void vote(VoteDto voteDto) {
        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post Not Found with ID - " + voteDto.getPostId()));
        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());

        if(voteByPostAndUser.isPresent() &&
                voteByPostAndUser.get().getVoteType()==voteDto.getVoteType())
            throw new SpringRedditException("You already have "
                    + voteDto.getVoteType() + "'d for this post");

        if(VoteType.UPVOTE==voteDto.getVoteType())
            post.setVoteCount(post.getVoteCount()+1);
        else
            post.setVoteCount(post.getVoteCount()-1);

        voteRepository.save(mapToVote(voteDto, post));
        postRepository.save(post);
    }

    private Vote mapToVote(VoteDto voteDto, Post post){
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}
