package cn.itcast.core.listener;

import cn.itcast.core.mapper.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.List;


//保存索引库
public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemDao itemDao;
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage atm = (ActiveMQTextMessage) message;

        try {
            String id = atm.getText();
            System.out.println("保存索引库: " + id);

            //TODO 保存索引库
            ItemQuery query = new ItemQuery();
            query.createCriteria().andGoodsIdEqualTo(Long.parseLong(id)).andIsDefaultEqualTo("1");
            List<Item> itemOne = itemDao.selectByExample(query); //实际就一条数据,默认收搜的那条
            solrTemplate.saveBeans(itemOne, 1000);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
