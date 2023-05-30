package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;

import java.util.List;

public interface LabelService {
    List<Label> getAllLabel();

    Label getLabelById(long id);

    Label createLabel(LabelDto labelDto);

    Label updateLabelById(long id, LabelDto labelDto);

    void deleteLabelById(long id);
}
