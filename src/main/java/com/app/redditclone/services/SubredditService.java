package com.app.redditclone.services;

import com.app.redditclone.dto.SubredditDto;
import com.app.redditclone.exceptions.SpringRedditException;
import com.app.redditclone.mapper.SubredditMapper;
import com.app.redditclone.model.Subreddit;
import com.app.redditclone.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {
    private final SubredditRepository subredditRepository;
    private final SubredditMapper subredditMapper;

    @Transactional
    public SubredditDto save(SubredditDto subredditDto){
        Subreddit save = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));
        subredditDto.setId(save.getId());
        return subredditDto;
    }

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll().stream().map(subredditMapper::mapSubredditToDto).collect(Collectors.toList());
    }

    public SubredditDto getSubredditById(Long id) {
        Subreddit subreddit = subredditRepository.findById(id).orElseThrow(() -> new SpringRedditException("No subreddit found with ID - " + id));
        return subredditMapper.mapSubredditToDto(subreddit);
    }
}
