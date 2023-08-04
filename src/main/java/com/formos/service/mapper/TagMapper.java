package com.formos.service.mapper;

import com.formos.service.dto.clickup.SubCategoryDTO;
import com.formos.service.dto.clickup.TagDTO;
import com.formos.service.dto.clickup.Task;
import com.formos.service.dto.clickup.Team;
import com.formos.service.utils.CommonUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TagMapper {

    public TagDTO toTagDTO(Task.Tag tag) {
        TagDTO tagDTO = new TagDTO();
        tagDTO.setCreatorId(tag.creatorId);
        tagDTO.setName(tag.name);
        tagDTO.setTagBackground(tag.tagBackground);
        tagDTO.setTagForeground(CommonUtils.decreaseSaturation(tag.tagBackground));
        return tagDTO;
    }

    public List<TagDTO> toTagDTOs(List<Task.Tag> tags) {
        return tags.stream().map(this::toTagDTO).collect(Collectors.toList());
    }
}
