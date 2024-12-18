package com.ict.edu3.domain.guestbook.controller;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ict.edu3.domain.auth.vo.DataVO;
import com.ict.edu3.domain.guestbook.service.GuestBookService;
import com.ict.edu3.domain.guestbook.vo.GuestBookVO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RestController
@RequestMapping("/api/guestbook")
public class GuestBookController {
    @Autowired
    private GuestBookService guestBookService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/list")
    public DataVO getGuestBookList() {
        DataVO dataVO = new DataVO();
        try {
            List<GuestBookVO> list = guestBookService.getGuestBookList();
            dataVO.setSuccess(true);
            dataVO.setMessage("게스트북 조회 성공");
            dataVO.setData(list);
        } catch (Exception e) {
            dataVO.setSuccess(false);
            dataVO.setMessage("게스트북 조회 실패");
        }
        return dataVO;
    }

    @GetMapping("/detail/{gb_idx}")
    public DataVO getGuestBookDetail(@PathVariable String gb_idx) {
        DataVO dataVO = new DataVO();
        try {
            // log.info("gb_idx :" + gb_idx);
            GuestBookVO gvo = guestBookService.getGuestBookById(gb_idx);
            if(gvo == null){
                dataVO.setSuccess(false);
                dataVO.setMessage("게스트북 상세보기 실패");
                return dataVO;
            }
            dataVO.setSuccess(true);
            dataVO.setMessage("게스트북 상세보기 성공");
            dataVO.setData(gvo);
        } catch (Exception e) {
            dataVO.setSuccess(false);
            dataVO.setMessage("게스트북 상세보기 실패");
        }
        return dataVO;
    }

    @GetMapping("/delete/{gb_idx}")
    public DataVO getGuestBookDelete(@PathVariable String gb_idx, Authentication authentication) {
        DataVO dataVO = new DataVO();
        try {
            //로그인 여부 확인
            if (authentication == null) {
                dataVO.setSuccess(false);
                dataVO.setMessage("로그인이 필요합니다.");
                return dataVO;
            }
            int result = guestBookService.getGuestBookDelete(gb_idx);
            if(result == 0){
                dataVO.setSuccess(false);
                dataVO.setMessage("게스트북 삭제 실패");
                return dataVO;
            }
            dataVO.setSuccess(true);
            dataVO.setMessage("게스트북 삭제 성공");
        } catch (Exception e) {
            dataVO.setSuccess(false);
            dataVO.setMessage("게스트북 삭제 오류 발생");
        }
        return dataVO;
    }

    @PutMapping("/update/{gb_idx}")
    public DataVO getGuestBookUpdate(@PathVariable String gb_idx, @RequestBody GuestBookVO gvo, Authentication authentication) {
        DataVO dataVO = new DataVO();
        try {
            //로그인 여부 확인
            if (authentication == null) {
                dataVO.setSuccess(false);
                dataVO.setMessage("로그인이 필요합니다.");
                return dataVO;
            }

            // 파라미터 확인
            int result = guestBookService.getGuestBookUpdate(gvo);
            // log.info("gb_idx : "+gb_idx+"\n");
            // log.info("gvo"+gvo+"\n");
            // log.info("auth"+authentication.getName()+"\n");
            // int result = 0;
            if(result == 0){
                dataVO.setSuccess(false);
                dataVO.setMessage("게스트북 수정 실패");
                return dataVO;
            }
            dataVO.setSuccess(true);
            dataVO.setMessage("게스트북 수정 성공");
        } catch (Exception e) {
            dataVO.setSuccess(false);
            dataVO.setMessage("게스트북 수정 오류 발생");
        }
        return dataVO;
    }

    @PostMapping("/write")
    public DataVO getGuestBookWrite(
            @ModelAttribute("data") GuestBookVO gvo,
            Authentication authentication) {

        DataVO dataVO = new DataVO();
        try {
            //로그인 여부 확인
            if (authentication == null) {
                dataVO.setSuccess(false);
                dataVO.setMessage("로그인이 필요합니다.");
                return dataVO;
            }
            // 로그인한 사람의 id 추출
            gvo.setGb_id(authentication.getName());
            gvo.setGb_pw(passwordEncoder.encode(gvo.getGb_pw()));
            // 파라미터 확인
          
            MultipartFile file = gvo.getFile();

            if (file.isEmpty()) {
                gvo.setGb_filename("");
            }else{
                UUID uuid = UUID.randomUUID();
                String f_name = uuid.toString() + "_" + file.getOriginalFilename();
                gvo.setGb_filename(f_name);

                // 프로젝트 내부의 resources/static/upload 경로
                String path = new File("src/main/resources/static/upload").getAbsolutePath();
                // 실질적인 파일업로드
                file.transferTo(new File(path, f_name));
            }

            // 게스트북 쓰기
            int result = guestBookService.getGuestBookWrite(gvo);
            if(result == 0){
                dataVO.setSuccess(false);
                dataVO.setMessage("게스트북 쓰기 실패");
                return dataVO;
            }
            dataVO.setSuccess(true);
            dataVO.setMessage("게스트북 쓰기 성공");
        } catch (Exception e) {
            dataVO.setSuccess(false);
            dataVO.setMessage("게스트북 쓰기 오류 발생");
        }
        return dataVO;
    }
    
    
}
