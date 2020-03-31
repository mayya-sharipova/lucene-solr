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

/**
 * Different modes of search.
 */
public enum ScoreMode {
  
  /**
   * Produced scorers will allow visiting all matches and get their score.
   */
  COMPLETE {
    @Override
    public boolean needsScores() {
      return true;
    }
    @Override
    public boolean isExhaustive() {
      return true;
    }
  },

  /**
   * Produced scorers will allow visiting all matches but scores won't be
   * available.
   */
  COMPLETE_NO_SCORES {
    @Override
    public boolean needsScores() {
      return false;
    }
    @Override
    public boolean isExhaustive() {
      return true;
    }
  },

  /**
   * Produced scorers will optionally allow skipping over non-competitive
   * hits using the {@link Scorer#setMinCompetitiveScore(float)} API.
   */
  TOP_SCORES {
    @Override
    public boolean needsScores() {
      return true;
    }
    @Override
    public boolean isExhaustive() {
      return false;
    }
  },

  /**
   * ScoreMode for top field collectors that can provide their own iterators,
   * to optionally allow to skip for non-competitive docs
   */
  TOP_DOCS {
    @Override
    public boolean needsScores() {
      return false;
    }
    @Override
    public boolean isExhaustive() {
      return false;
    }
  },

  /**
   * ScoreMode for top field collectors that can provide their own iterators,
   * to optionally allow to skip for non-competitive docs.
   * This mode is used when there is a secondary sort by _score.
   */
  TOP_DOCS_WITH_SCORES {
    @Override
    public boolean needsScores() {
      return true;
    }
    @Override
    public boolean isExhaustive() {
      return false;
    }
  };

  /**
   * Whether this {@link ScoreMode} needs to compute scores.
   */
  public abstract boolean needsScores();

  /**
   * Returns {@code true} if for this {@link ScoreMode} it is necessary to process all documents,
   * or {@code false} if is enough to go through top documents only.
   */
  public abstract boolean isExhaustive();
}
