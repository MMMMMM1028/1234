package com.ManageServices.service;

import com.ManageServices.dao.*;
import com.ManageServices.service_interface.PaperService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = PaperService.class)
@Component
public class PaperServiceImpl implements PaperService {
    @Autowired
    PaperMapper pm;
    @Autowired
    ExpertPaperMapper epm;
    @Autowired
    UserMapper um;
    @Autowired
    OrderMapper om;
    @Autowired
    ExpertMapper em;
    @Autowired
    ExpertExpertMapper eem;

    @Override
    @Transactional
    public List insert(List<Map> list){
        List expertIdList = new ArrayList();
        for (Map map : list){
            expertIdList.add(upload(map));
        }
        return expertIdList;
    }

    private int upload(Map paper){
        List<Map> authorList = (List<Map>)paper.get("author");
        //移处author，插入数据库paper_
        paper.remove("author");

        //将论文插入论文表，todo 假设没有重复
        pm.insertPaperByMap(paper);
        int paperId = (int) paper.get("paperId");
        int count = 0;
        //论文作者不超过6个
        int[] authorIdList = new int[6];
        //遍历专家插入专家表，获得专家的expertId
        //todo 不存专家作者，因为不做查询
        for (Map expert : authorList){
            Map temp = em.selectExpertIdByInf(expert);
            if(temp == null) { //该专家不存在，插入
                //插入后返回自增主键
                em.insertExpertByMap(expert);
                authorIdList[count++] = (int) expert.get("expertId");
            }else{//已经存在，返回主键expertId
                authorIdList[count++] = (int) temp.get("expertId");
            }
        }

        //遍历获得的专家IDList，更新之间的专家关系，
        //根据作者ID，和论文ID插入论文专家关系表
        for (int i = 0; i < count; i++){
            int expertId = authorIdList[i];
            //添加专家论文关系
            epm.insertExpertPaper(paperId,expertId);
            for (int j = i + 1; j < count; j++){
                int expertId2 = authorIdList[j];
                //添加专家专家关系
                updateExpertExpertShip(expertId, expertId2);
            }
        }
        return paperId;
    }
    private int updateExpertExpertShip(int expertId1, int expertId2){
        String isExisted = eem.isExisted(expertId1,expertId2);
        if(isExisted == null){//专家关系不存在，则插入
            return eem.insertExpertExpert(expertId1,expertId2);
        }else{
            return eem.updateCount(expertId1,expertId2);
        }
    }

    @Override
    @Transactional

    public int uploadPaper(Map paper) {
        upload(paper);
        return 0;
    }

//    @Override
//    public int insertPaperByBatch(List<Map> list) {
//        return pm.insertByBatch(list);
//    }
//
//
//    @Override
//    public int insertExpertPaperByBatch(List<Map> list) {
//        return epm.insertByBatch(list);
//    }


    @Transactional
    @Override
    public String download(int paperId, int userId) {
        Map paper = pm.selectPaperDetial(paperId);
        int price = (int)paper.get("price");
        if(price != 0){
            //是否购买过
            List<Map> order = om.selectOrder(userId,paperId,null,null);
            if(order == null){
                //没有买过，不允许下载
                return null;
            }
        }
        //允许下载，下载次数++ 返回下载路径
        pm.updatePaper(paperId,-1,1);
        return (String)paper.get("filepath");
    }

    @Transactional(readOnly = true)
    @Override
    public Map selectPaperHomeByPid(int paperId){
        Map paper = pm.selectPaperDetial(paperId);
        return  paper;
    }

    @Transactional
    @Override
    public int changePrice(int paperId, int price) {
        return pm.updatePaper(paperId,price,-1);
    }

}
