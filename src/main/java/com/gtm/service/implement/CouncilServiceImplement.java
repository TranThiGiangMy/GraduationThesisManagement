package com.gtm.service.implement;

import com.gtm.constants.SystemConstant;
import com.gtm.pojo.Council;
import com.gtm.pojo.Student;
import com.gtm.pojo.Thesis;
import com.gtm.repository.CouncilRepository;
import com.gtm.repository.ThesisRepository;
import com.gtm.service.CouncilService;
import com.gtm.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CouncilServiceImplement implements CouncilService {
    @Autowired
    private CouncilRepository councilRepository;
    @Autowired
    private ThesisRepository thesisRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public List<Council> getCouncils(Map<String, String> params) {
        return this.councilRepository.getCouncils(params);
    }

    @Override
    public Council getCouncilById(int councilId) {
        return this.councilRepository.getCouncilById(councilId);
    }

    @Override
    public long countCouncil(Map<String, String> params) {
        return this.councilRepository.countCouncil(params);
    }

    @Override
    public long countAllCouncil() {
        return this.councilRepository.countAllCouncil();
    }

    @Override
    public boolean addCouncil(Council council) {
        return this.councilRepository.addCouncil(council);
    }

    @Override
    public boolean deleteCouncil(int councilId) {
        return this.councilRepository.deleteCouncil(councilId);
    }

    @Override
    public List<Object[]> scoreDetailOfCouncilForThesis(int councilId, int thesisId) {
        return this.councilRepository.scoreDetailOfCouncilForThesis(councilId, thesisId);
    }

    @Override
    public void sendThesisResultNotification(Council council) {
        List<String> toEmail;
        Map<String, Object> model;

        for (Thesis thesis : council.getTheses()) {
            toEmail = new ArrayList<>();
            model = new HashMap<>();

            model.put("thesis", thesis);

            for (Student student : thesis.getStudents()) {
                toEmail.add(student.getEmail());
            }

            this.emailService.sendMail("Th??ng b??o k???t qu??? kh??a lu???n t???t nghi???p",
                    toEmail.toArray(new String[]{}), model, SystemConstant.THESIS_RESULT_EMAIL_TEMPLATE);
        }
    }


    @Override
    public boolean lockOrUnlockCouncil(int councilId, boolean block) {
        Council council = this.councilRepository.lockOrUnlockCouncil(councilId, block);
        if (council != null) {
            if (block) {
                Council councilResult = this.thesisRepository.thesisResult(council.getId());
                if (councilResult != null) {
                    this.sendThesisResultNotification(councilResult);

                    return true;
                }
            }
            return true;
        }
        return false;
    }

}
