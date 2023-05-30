package hexlet.code.service.impl;

import hexlet.code.dto.LabelDto;
import hexlet.code.exception.LabelNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private LabelRepository labelRepository;

    @Override
    public List<Label> getAllLabel() {
        return labelRepository.findAll();
    }

    @Override
    public Label getLabelById(long id) {
        return labelRepository.findById(id).orElseThrow(() -> new LabelNotFoundException("Label not found"));
    }

    @Override
    public Label createLabel(LabelDto labelDto) {
        Label label = new Label();
        label.setName(labelDto.getName());
        return labelRepository.save(label);
    }

    @Override
    public Label updateLabelById(long id, LabelDto labelDtoUpdated) {
        Label labelToBeUpdated = labelRepository.findById(id).get();
        labelToBeUpdated.setName(labelDtoUpdated.getName());
        return labelRepository.save(labelToBeUpdated);
    }

    @Override
    public void deleteLabelById(long id) {
        labelRepository.deleteById(id);
    }
}
