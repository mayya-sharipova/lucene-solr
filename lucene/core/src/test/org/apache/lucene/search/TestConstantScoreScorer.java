/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.lucene.search;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.RandomIndexWriter;
import org.apache.lucene.store.Directory;

import org.apache.lucene.util.LuceneTestCase;

public class TestConstantScoreScorer extends  LuceneTestCase{

  public void testMinCompetitiveScore () throws IOException {
    Directory dir = newDirectory();
    RandomIndexWriter writer = new RandomIndexWriter(random(), dir);
    Document doc = new Document();
    Field field = newTextField("field", "value1", Field.Store.NO);
    doc.add(field);
    writer.addDocument(doc);
    writer.commit();

    DirectoryReader reader = writer.getReader();
    final IndexSearcher searcher = newSearcher(reader);
    Query query = new FakeQuery();

    Scorer scorer = searcher
        .createWeight(query, ScoreMode.TOP_SCORES, 1)
        .scorer(searcher.getIndexReader().leaves().get(0));
    scorer.setMinCompetitiveScore(1);
    assertEquals(0, scorer.iterator().nextDoc());

    scorer = searcher
        .createWeight(query, ScoreMode.TOP_SCORES, 1)
        .scorer(searcher.getIndexReader().leaves().get(0));
    scorer.setMinCompetitiveScore(2);
    assertEquals(DocIdSetIterator.NO_MORE_DOCS, scorer.iterator().nextDoc());

    reader.close();
    writer.close();
    dir.close();
  }


  private static class FakeQuery extends Query {
    @Override
    public Weight createWeight(IndexSearcher searcher, ScoreMode scoreMode, float boost) throws IOException {
      return new ConstantScoreWeight(this, boost) {
        @Override
        public Scorer scorer(LeafReaderContext context) throws IOException {
          return scorerSupplier(context).get(Long.MAX_VALUE);
        }

        @Override
        public boolean isCacheable(LeafReaderContext ctx) {
          return true;
        }

        @Override
        public ScorerSupplier scorerSupplier(LeafReaderContext context) throws IOException {
          final Weight weight = this;
          return new ScorerSupplier() {
            @Override
            public Scorer get(long leadCost) throws IOException {
              return new ConstantScoreScorer(weight, boost, DocIdSetIterator.all(context.reader().maxDoc()));
            }
            @Override
            public long cost() {
              return 1;
            }
          };
        }
      };
    }

    @Override
    public boolean equals(Object other) {
      return sameClassAs(other);
    }

    @Override
    public int hashCode() {
      return 0;
    }

    @Override
    public String toString(String field) {
      return "FakeQuery";
    }

  }
}


