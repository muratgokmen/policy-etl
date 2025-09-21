package com.etl.policy.batch.reader;

import com.etl.policy.entity.document.PdfStore;
import com.etl.policy.repository.document.PdfStoreRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class PdfReader implements ItemReader<PdfStore> {
  @Autowired
  PdfStoreRepository repo;
  private Iterator<PdfStore> it;

  @Override
  public PdfStore read() {
    if (it == null) {
      List<PdfStore> list = repo.findUnprocessed(); // custom query
      it = list.iterator();
    }
    return it.hasNext() ? it.next() : null;
  }
}
